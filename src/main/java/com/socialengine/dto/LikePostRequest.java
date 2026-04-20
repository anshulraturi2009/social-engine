package com.socialengine.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikePostRequest {

    @NotNull(message = "userId is required.")
    @Positive(message = "userId must be positive.")
    private Long userId;
}
