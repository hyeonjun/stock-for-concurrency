package com.example.stock.named.helper;

import com.example.stock.named.domain.StockNamed;
import com.example.stock.named.repository.StockNamedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockStockHelper {

  private final StockNamedRepository stockNamedRepository;

  // 부모의 트랜잭션와 별도로 실행되어야 하므로 propagation 을 REQUIRES_NEW 설정
  @Transactional(value = "namedLockTransactionManager", propagation = Propagation.REQUIRES_NEW)
  public void decrease(long id, Long quantity) {
    StockNamed stock = stockNamedRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    stock.decrease(quantity);
    stockNamedRepository.save(stock);
  }

}
