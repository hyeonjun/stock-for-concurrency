package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

  private final StockRepository stockRepository;

  @Transactional
  public void decrease(Long id, Long quantity) {
    // Stock 조회
    Stock stock = stockRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    // 재고 감소
    stock.decrease(quantity);

    // 갱신된 값을 저장
    stockRepository.save(stock);
  }

}
