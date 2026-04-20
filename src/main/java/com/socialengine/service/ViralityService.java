package com.socialengine.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViralityService {

    private final StringRedisTemplate stringRedisTemplate;

    public ViralityService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public long incrementBotReply(Long postId) {
        return increment(postId, 1);
    }

    public long incrementHumanComment(Long postId) {
        return increment(postId, 50);
    }

    public long incrementLike(Long postId) {
        return increment(postId, 20);
    }

    public void decrement(Long postId, long delta) {
        Long updatedScore = stringRedisTemplate.opsForValue().increment("post:" + postId + ":virality_score", -delta);
        if (updatedScore == null) {
            throw new IllegalStateException("Unable to roll back virality score.");
        }
    }

    public long getCurrentScore(Long postId) {
        String value = stringRedisTemplate.opsForValue().get("post:" + postId + ":virality_score");
        if (value == null || value.isBlank()) {
            return 0L;
        }
        return Long.parseLong(value);
    }

    private long increment(Long postId, long delta) {
        Long updatedScore = stringRedisTemplate.opsForValue().increment("post:" + postId + ":virality_score", delta);
        if (updatedScore == null) {
            throw new IllegalStateException("Unable to update virality score.");
        }
        return updatedScore;
    }
}
