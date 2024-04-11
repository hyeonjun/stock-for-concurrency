package com.example.stock.redis.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

  private final RedisTemplate<String, String> redisTemplate;

  public Boolean lock(String key) {
    return redisTemplate
      .opsForValue()
      .setIfAbsent(key, "lock", Duration.ofMillis(3_000)); // key, value, duration
  }

  public Boolean unlock(String key) {
    return redisTemplate.delete(key);
  }

  // 로직 실행 전에 key와 setnx 명령어를 활용해서 락을 하고 로직이 끝나면 언락 메소드를 통해서 락을 해제하는 방식
  // 레디스를 활용하는 방식도 로직 실행 전후로 락 획득 해제를 수행해줘야 되기 때문에
  // 헬퍼 서비스를 활용

}