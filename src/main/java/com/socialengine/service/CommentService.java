package com.socialengine.service;

import com.socialengine.dto.CreateCommentRequest;
import com.socialengine.entity.Comment;
import com.socialengine.entity.Post;
import com.socialengine.repository.CommentRepository;
import com.socialengine.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final GuardrailService guardrailService;
    private final ViralityService viralityService;
    private final NotificationService notificationService;
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentService(
        CommentRepository commentRepository,
        PostRepository postRepository,
        GuardrailService guardrailService,
        ViralityService viralityService,
        NotificationService notificationService
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.guardrailService = guardrailService;
        this.viralityService = viralityService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Comment createComment(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found for id " + postId));

        String authorType = normalizeAuthorType(request.getAuthorType());
        if ("BOT".equals(authorType)) {
            validateBotCommentRequest(request);
            GuardrailService.BotCommentReservation reservation = guardrailService.reserveBotComment(
                postId,
                request.getDepthLevel(),
                request.getBotId(),
                request.getHumanId()
            );

            try {
                Comment savedComment = persistComment(postId, request, authorType);
                viralityService.incrementBotReply(postId);
                sendNotificationSafely(post, request);
                return savedComment;
            } catch (RuntimeException exception) {
                guardrailService.rollbackBotComment(postId, reservation);
                throw exception;
            }
        }

        Comment savedComment = persistComment(postId, request, authorType);
        viralityService.incrementHumanComment(postId);
        return savedComment;
    }

    private Comment persistComment(Long postId, CreateCommentRequest request, String authorType) {
        Comment comment = Comment.builder()
            .postId(postId)
            .authorId(request.getAuthorId())
            .authorType(authorType)
            .content(request.getContent().trim())
            .depthLevel(request.getDepthLevel())
            .build();

        return commentRepository.save(comment);
    }

    private void validateBotCommentRequest(CreateCommentRequest request) {
        if (request.getBotId() == null || request.getHumanId() == null) {
            throw new IllegalArgumentException("botId and humanId are required for bot comments.");
        }
        if (!request.getAuthorId().equals(request.getBotId())) {
            throw new IllegalArgumentException("authorId must match botId for bot comments.");
        }
    }

    private void sendNotificationSafely(Post post, CreateCommentRequest request) {
        if ("USER".equals(post.getAuthorType()) && request.getHumanId() != null) {
            try {
                notificationService.processBotCommentNotification(request.getHumanId(), request.getBotId());
            } catch (RuntimeException exception) {
                logger.warn("Notification dispatch failed for user {} after bot comment.", request.getHumanId(), exception);
            }
        }
    }

    private String normalizeAuthorType(String authorType) {
        String normalizedAuthorType = authorType.trim().toUpperCase();
        if (!"USER".equals(normalizedAuthorType) && !"BOT".equals(normalizedAuthorType)) {
            throw new IllegalArgumentException("authorType must be USER or BOT.");
        }
        return normalizedAuthorType;
    }
}
