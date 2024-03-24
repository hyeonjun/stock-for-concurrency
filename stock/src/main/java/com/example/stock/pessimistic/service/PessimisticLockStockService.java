package com.example.stock.pessimistic.service;

import com.example.stock.pessimistic.domain.StockPessimistic;
import com.example.stock.pessimistic.repository.StockPessimisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessimisticLockStockService {

  private final StockPessimisticRepository stockRepository;

  @Transactional(readOnly = true)
  public StockPessimistic getStock(Long id) {
    return stockRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  @Transactional
  public void decrease(long id, Long quantity) {
    StockPessimistic stock = stockRepository.findByIdForUpdate(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stock.decrease(quantity);
    stockRepository.save(stock);
  }

}
