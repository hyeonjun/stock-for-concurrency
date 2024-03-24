package com.example.stock.synchronize.service;

import com.example.stock.synchronize.domain.StockSync;
import com.example.stock.synchronize.repository.StockSyncRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SynchronizedStockService {

  private final StockSyncRepository stockSyncRepository;

  @Transactional(readOnly = true)
  public StockSync getStock(Long id) {
    return stockSyncRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  @Transactional
  public void decrease(Long id, Long quantity) {
    // Stock 조회
    StockSync stock = stockSyncRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    // 재고 감소
    stock.decrease(quantity);

    // 갱신된 값을 저장
    stockSyncRepository.save(stock);
  }

  /**
   * synchronized 키워드를 메소드 선언부에 붙여주면 한 개의 스레드만 접근가능하게 한다.
   * synchronized 사용시 @Transactional과 동시에 사용하면 안된다.
   * Synchronized를 사용하는 이유는 해당 메소드를 한 쓰레드에서만 돌리기 위해서다.
   * 하지만, 트랜잭션이 같이 정의가 되어있다면 첫 번째 쓰레드가 끝나기 전 두 번째 쓰레드가 발동할 수도 있다.
   */
  public synchronized void decreaseSynchronized(Long id, Long quantity) {
    // Stock 조회
    StockSync stock = stockSyncRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    // 재고 감소
    stock.decrease(quantity);

    // 갱신된 값을 저장
    stockSyncRepository.save(stock);
  }

}
