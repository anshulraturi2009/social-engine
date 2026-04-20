package com.socialengine.scheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationSweeper {

    private final StringRedisTemplate stringRedisTemplate;
    private final Logger logger = LoggerFactory.getLogger(NotificationSweeper.class);

    public NotificationSweeper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Scheduled(fixedRate = 300000)
    public void sweepPendingNotifications() {
        List<String> pendingNotificationKeys = findPendingNotificationKeys();
        int processedUsers = 0;

        for (String listKey : pendingNotificationKeys) {
            Long listSize = stringRedisTemplate.opsForList().size(listKey);
            String botName = stringRedisTemplate.opsForList().index(listKey, 0);

            if (listSize != null && listSize > 0 && botName != null) {
                logger.info(
                    "Summarized Push Notification: {} and {} others interacted with your posts.",
                    botName,
                    listSize - 1
                );
                processedUsers++;
            }

            stringRedisTemplate.delete(listKey);
        }

        logger.info("[CRON] Sweep complete. Processed {} users.", processedUsers);
    }

    private List<String> findPendingNotificationKeys() {
        List<String> keys = new ArrayList<>();
        stringRedisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(
                ScanOptions.scanOptions().match("user:*:pending_notifs").count(100).build()
            )) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to scan pending notification keys.", exception);
            }
            return null;
        });
        return keys;
    }
}
