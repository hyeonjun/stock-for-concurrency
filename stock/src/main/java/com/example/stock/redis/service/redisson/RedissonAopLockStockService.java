package com.example.stock.redis.service.redisson;

import com.example.stock.redis.aop.distributelock.DistributedLock;
import com.example.stock.redis.domain.StockRedis;
import com.example.stock.redis.helper.RedisLockStockHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedissonAopLockStockService {

  private final RedisLockStockHelper redisLockStockHelper;

  @Transactional(readOnly = true)
  public StockRedis getStock(Long id) {
    return redisLockStockHelper.getStock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  @DistributedLock(key = "#stockRedis.getUuid()")
  public void decrease(StockRedis stockRedis, Long quantity) {
    redisLockStockHelper.decrease(stockRedis.getId(), quantity);
  }

}
