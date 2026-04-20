package com.socialengine.service;

import com.socialengine.entity.Bot;
import com.socialengine.repository.BotRepository;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final StringRedisTemplate stringRedisTemplate;
    private final BotRepository botRepository;
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(StringRedisTemplate stringRedisTemplate, BotRepository botRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.botRepository = botRepository;
    }

    public void processBotCommentNotification(Long humanId, Long botId) {
        String cooldownKey = "notif_cooldown:user_" + humanId;
        String botName = botRepository.findById(botId)
            .map(Bot::getName)
            .filter(name -> name != null && !name.isBlank())
            .orElse("Bot " + botId);

        Boolean cooldownExists = stringRedisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(cooldownExists)) {
            String listKey = "user:" + humanId + ":pending_notifs";
            stringRedisTemplate.opsForList().rightPush(listKey, botName);
            return;
        }

        logger.info("Push Notification Sent to User {}", humanId);
        stringRedisTemplate.opsForValue()
            .set("notif_cooldown:user_" + humanId, "1", Duration.ofSeconds(900));
    }
}
