package org.example.mebkuch.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouriteRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "favourites:";

    public void add(Long userId, Long productId) {
        redisTemplate.opsForSet().add(KEY_PREFIX + userId, productId.toString());
    }

    public void remove(Long userId, Long productId) {
        redisTemplate.opsForSet().remove(KEY_PREFIX + userId, productId.toString());
    }

    public Set<Long> getAll(Long userId) {
        Set<String> raw = redisTemplate.opsForSet().members(KEY_PREFIX + userId);

        if (raw == null) return Set.of();

        return raw.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean exists(Long userId, Long productId) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(KEY_PREFIX + userId, productId.toString())
        );
    }
}