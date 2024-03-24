package com.example.stock.optimistic.helper;

import com.example.stock.optimistic.domain.StockOptimistic;
import com.example.stock.optimistic.repository.StockOptimisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OptimisticLockStockHelper {

  @Autowired
  private final StockOptimisticRepository stockOptimisticRepository;

  @Transactional
  public void decrease(long id, Long quantity) {
    StockOptimistic stock = stockOptimisticRepository.findByIdLock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stock.decrease(quantity);
    stockOptimisticRepository.save(stock);
  }

}
