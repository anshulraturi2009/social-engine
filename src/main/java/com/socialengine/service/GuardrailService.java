package com.socialengine.service;

import com.socialengine.exception.GuardrailException;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GuardrailService {

    private final StringRedisTemplate stringRedisTemplate;

    public GuardrailService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public BotCommentReservation reserveBotComment(Long postId, Integer depthLevel, Long botId, Long humanId) {
        if (depthLevel == null || depthLevel > 20) {
            throw new GuardrailException(
                "VERTICAL_CAP",
                "Comment depth limit of 20 exceeded",
                HttpStatus.TOO_MANY_REQUESTS
            );
        }

        Long newCount = stringRedisTemplate.opsForValue()
            .increment("post:" + postId + ":bot_count");
        if (newCount == null) {
            throw new IllegalStateException("Unable to evaluate horizontal cap.");
        }
        if (newCount > 100) {
            stringRedisTemplate.opsForValue()
                .decrement("post:" + postId + ":bot_count");
            throw new GuardrailException(
                "HORIZONTAL_CAP",
                "Bot reply limit of 100 reached for this post",
                HttpStatus.TOO_MANY_REQUESTS
            );
        }

        String cooldownKey = "cooldown:bot_" + botId + ":human_" + humanId;
        Boolean isNew = stringRedisTemplate.opsForValue()
            .setIfAbsent(cooldownKey, "1", Duration.ofSeconds(600));
        if (Boolean.FALSE.equals(isNew)) {
            stringRedisTemplate.opsForValue()
                .decrement("post:" + postId + ":bot_count");
            throw new GuardrailException(
                "COOLDOWN_CAP",
                "This bot cannot interact with this user for 10 minutes",
                HttpStatus.TOO_MANY_REQUESTS
            );
        }
        if (isNew == null) {
            stringRedisTemplate.opsForValue()
                .decrement("post:" + postId + ":bot_count");
            throw new IllegalStateException("Unable to evaluate cooldown guardrail.");
        }

        return new BotCommentReservation(cooldownKey, true, true);
    }

    public void rollbackBotComment(Long postId, BotCommentReservation reservation) {
        if (reservation == null) {
            return;
        }
        if (reservation.isHorizontalCapReserved()) {
            Long updatedCount = stringRedisTemplate.opsForValue().decrement("post:" + postId + ":bot_count");
            if (updatedCount != null && updatedCount < 0) {
                stringRedisTemplate.opsForValue().set("post:" + postId + ":bot_count", "0");
            }
        }
        if (reservation.isCooldownReserved() && reservation.getCooldownKey() != null) {
            stringRedisTemplate.delete(reservation.getCooldownKey());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class BotCommentReservation {

        private String cooldownKey;
        private boolean horizontalCapReserved;
        private boolean cooldownReserved;
    }
}
