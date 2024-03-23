package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessimisticLockStockService {

  private final StockRepository stockRepository;

  @Transactional
  public void decrease(long id, Long quantity) {
    Stock stock = stockRepository.findByIdForUpdate(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stock.decrease(quantity);
    stockRepository.save(stock);
  }

}
