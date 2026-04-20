package com.socialengine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {

    @NotNull(message = "authorId is required.")
    @Positive(message = "authorId must be positive.")
    private Long authorId;

    @NotBlank(message = "authorType is required.")
    @Pattern(regexp = "USER|BOT", message = "authorType must be USER or BOT.")
    private String authorType;

    @NotBlank(message = "content is required.")
    private String content;

    @NotNull(message = "depthLevel is required.")
    @Min(value = 1, message = "depthLevel must be at least 1.")
    private Integer depthLevel;

    @Positive(message = "botId must be positive when provided.")
    private Long botId;

    @Positive(message = "humanId must be positive when provided.")
    private Long humanId;
}
