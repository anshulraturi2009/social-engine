package com.socialengine.service;

import com.socialengine.dto.CreatePostRequest;
import com.socialengine.entity.Post;
import com.socialengine.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ViralityService viralityService;

    public PostService(PostRepository postRepository, ViralityService viralityService) {
        this.postRepository = postRepository;
        this.viralityService = viralityService;
    }

    @Transactional
    public Post createPost(CreatePostRequest request) {
        Post post = Post.builder()
            .authorId(request.getAuthorId())
            .authorType(normalizeAuthorType(request.getAuthorType()))
            .content(request.getContent().trim())
            .build();

        return postRepository.save(post);
    }

    public long likePost(Long postId, Long userId) {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("userId must be positive.");
        }
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found for id " + postId);
        }
        return viralityService.incrementLike(postId);
    }

    private String normalizeAuthorType(String authorType) {
        String normalizedAuthorType = authorType.trim().toUpperCase();
        if (!"USER".equals(normalizedAuthorType) && !"BOT".equals(normalizedAuthorType)) {
            throw new IllegalArgumentException("authorType must be USER or BOT.");
        }
        return normalizedAuthorType;
    }
}
