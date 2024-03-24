package com.example.stock.optimistic.service;

import com.example.stock.optimistic.domain.StockOptimistic;
import com.example.stock.optimistic.helper.OptimisticLockStockHelper;
import com.example.stock.optimistic.repository.StockOptimisticRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

  private final StockOptimisticRepository stockOptimisticRepository;
  private final OptimisticLockStockHelper optimisticLockStockHelper;
  private final Logger log = LoggerFactory.getLogger(OptimisticLockStockService.class);

  @Transactional(readOnly = true)
  public StockOptimistic getStock(Long id) {
    return stockOptimisticRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  // @Transactional
  // 해당 메소드에서는 @Transactional을 붙이면 안된다.
  // 왜냐하면 낙관적 락을 사용하는 경우 version 차이로 인해 새로운 버전의 stock을 조회해야 하는데,
  // 하나의 트랜잭션으로 묶어서 사용하게 되면 재시도를 하는 경우에도 이전의 실패한 version의 Stock을
  // 1차 캐시에 가지고 있기 때문에 해당 version의 데이터만 조회되어 계속해서 update를 실패하게 됨
  public void decrease(long id, Long quantity) {
    boolean isSuccess = false;
    for(int i=0; i<100; i++) { // 실패 시 100번 재시도
//    while (true) {
      try {
        optimisticLockStockHelper.decrease(id, quantity);
        isSuccess = true;
        break; // 성공 시 종료하도록 break
      } catch (Exception e) {
        try {
          Thread.sleep(50); // 실패한 경우 50ms 기다린 후 재시도
        } catch (InterruptedException ie) {
          log.error(ie.getMessage());
        }
      }
    }

    if (!isSuccess) {
      throw new RuntimeException("재고 감소 실패, 확인 바람.");
    }
  }

}
