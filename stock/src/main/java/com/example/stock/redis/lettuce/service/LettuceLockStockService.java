package com.example.stock.redis.lettuce.service;

import com.example.stock.redis.lettuce.domain.StockRedisLettuce;
import com.example.stock.redis.lettuce.helper.LettuceLockStockHelper;
import com.example.stock.redis.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LettuceLockStockService {

  private final LettuceLockStockHelper lettuceLockStockHelper;
  private final RedisLockRepository redisLockRepository;

  @Transactional(readOnly = true)
  public StockRedisLettuce getStock(Long id) {
    return lettuceLockStockHelper.getStock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  public void decrease(Long id, Long quantity) {
    StockRedisLettuce stock = lettuceLockStockHelper
      .getStock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    int maxRetry = 100;
    int retry = 0;

    while(!redisLockRepository.lock(stock.getUuid())) {
      if (++retry == maxRetry) {
        throw new RuntimeException("락 획득 실패");
      }

      try {
        // Lock 획득에 실패한 경우 Thread Sleep 을 활용하여 100ms 텀을 두고
        // Lock 획득 재시도
        // 적절한 텀을 두어야 Redis 의 부하를 줄일 수 있다.
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    try {
      lettuceLockStockHelper.decrease(stock.getId(), quantity);
    } finally {
      // 로직 수행 후 unlock 메소드를 활용하여 락 해제
      redisLockRepository.unlock(stock.getUuid());
    }
  }

}


