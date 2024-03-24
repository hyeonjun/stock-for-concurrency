package com.example.stock.helper;

import com.example.stock.domain.StockVersion;
import com.example.stock.repository.StockVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OptimisticLockStockHelper {

  @Autowired
  private final StockVersionRepository stockVersionRepository;

  @Transactional
  public void decrease(long id, Long quantity) {
    StockVersion stock = stockVersionRepository.findByIdLock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stock.decrease(quantity);
    stockVersionRepository.save(stock);
  }

}
