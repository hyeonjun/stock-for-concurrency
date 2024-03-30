package com.example.stock.named.service;

import com.example.stock.named.domain.StockNamed;
import com.example.stock.named.helper.NamedLockStockHelper;
import com.example.stock.named.repository.StockNamedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NamedLockStockService {

  private final StockNamedRepository stockNamedRepository;
  private final NamedLockStockHelper namedLockStockHelper;

  @Transactional(value = "namedLockTransactionManager", readOnly = true)
  public StockNamed getStock(Long id) {
    return stockNamedRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  @Transactional(value = "namedLockTransactionManager")
  public void decrease(long id, String uuid, Long quantity) {
    StockNamed stockNamed = stockNamedRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    if (!stockNamed.getUuid().equals(uuid)) {
      throw new RuntimeException("올바른 uuid 가 아닙니다.");
    }

    try {
      stockNamedRepository.getLock(uuid);
      namedLockStockHelper.decrease(stockNamed.getId(), quantity);
    } finally {
      // 모든 로직이 종료된 후 락 해제
      stockNamedRepository.releaseLock(uuid);
    }
  }

}
