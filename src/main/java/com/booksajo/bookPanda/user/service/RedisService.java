package com.booksajo.bookPanda.user.service;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public String getData(String key) { // key를 통해 value(데이터)를 얻는다.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        //  duration 동안 (key, value)를 저장한다.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        // 데이터 삭제
        redisTemplate.delete(key);
    }

    public void saveToken(String key, Long userId, Date expirationDate) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 현재 시간과 만료 시간의 차이를 계산하여 지속 시간(duration)을 설정합니다.
        long duration = expirationDate.getTime() - System.currentTimeMillis();
        if (duration <= 0) {
            // 만약 duration이 0 이하라면 토큰을 저장할 필요가 없다.
            return;
        }

        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key, String.valueOf(userId), expireDuration);
    }
}