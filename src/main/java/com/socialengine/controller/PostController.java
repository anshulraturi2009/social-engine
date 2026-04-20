package com.socialengine.controller;

import com.socialengine.dto.ApiResponse;
import com.socialengine.dto.CreateCommentRequest;
import com.socialengine.dto.CreatePostRequest;
import com.socialengine.dto.LikePostRequest;
import com.socialengine.entity.Comment;
import com.socialengine.entity.Post;
import com.socialengine.service.CommentService;
import com.socialengine.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(
        @PathVariable @Positive(message = "postId must be positive.") Long postId,
        @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, request));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> likePost(
        @PathVariable @Positive(message = "postId must be positive.") Long postId,
        @Valid @RequestBody LikePostRequest request
    ) {
        long updatedViralityScore = postService.likePost(postId, request.getUserId());
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
            .message("Virality score updated")
            .status(HttpStatus.OK.value())
            .data(Map.of(
                "postId", postId,
                "viralityScore", updatedViralityScore
            ))
            .build();

        return ResponseEntity.ok(response);
    }
}
