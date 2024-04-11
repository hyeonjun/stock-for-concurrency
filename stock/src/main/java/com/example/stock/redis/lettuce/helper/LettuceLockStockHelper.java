package com.example.stock.redis.lettuce.helper;

import com.example.stock.redis.lettuce.domain.StockRedisLettuce;
import com.example.stock.redis.lettuce.repository.StockRedisLettuceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LettuceLockStockHelper {

  private final StockRedisLettuceRepository stockRedisLettuceRepository;

  @Transactional
  public Optional<StockRedisLettuce> getStock(Long id) {
    return stockRedisLettuceRepository.findById(id);
  }

  @Transactional
  public void decrease(Long id, Long quantity) {
    StockRedisLettuce stock = stockRedisLettuceRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stock.decrease(quantity);
    stockRedisLettuceRepository.save(stock);
  }

}
