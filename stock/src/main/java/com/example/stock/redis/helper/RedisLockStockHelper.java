package com.example.stock.redis.helper;

import com.example.stock.redis.domain.StockRedis;
import com.example.stock.redis.repository.StockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisLockStockHelper {

  private final StockRedisRepository stockRedisRepository;

  @Transactional
  public Optional<StockRedis> getStock(Long id) {
    return stockRedisRepository.findById(id);
  }

  @Transactional
  public void decrease(Long id, Long quantity) {
    StockRedis stockRedis = stockRedisRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stockRedis.decrease(quantity);
    stockRedisRepository.save(stockRedis);
  }

}
