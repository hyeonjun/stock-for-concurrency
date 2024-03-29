package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stock.pessimistic.domain.StockPessimistic;
import com.example.stock.pessimistic.service.PessimisticLockStockService;
import com.example.stock.pessimistic.repository.StockPessimisticRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback(value = false)
public class PessimisticLockStockServiceTest {

  @Autowired
  private StockPessimisticRepository stockRepository;
  @Autowired
  private PessimisticLockStockService pessimisticLockStockService;

  private final Logger log = LoggerFactory.getLogger(PessimisticLockStockServiceTest.class);
  private final StockPessimistic initStock = new StockPessimistic(1L, 100L);

  @BeforeEach
  public void before() {
    stockRepository.save(initStock);
  }

  @AfterEach
  public void after() {
    stockRepository.delete(initStock);
  }

  @Test
  public void 동시에_재고감소_100개_요청_PessimisticLock() throws InterruptedException {
    log.info("stock id: {}, quantity: {}", initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    // 멀티스레드 이용 ExecutorService: 비동기를 단순하게 처리할 수 있도록 해주는 Java API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 다른 스레드에서 수행이 완료될 때까지 대기할 수 있도록 도와주는 API - 요청이 끝날 때까지 기다림
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          pessimisticLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage()); // 재고를 찾지 못하는 RuntimeException 발생
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockPessimistic stock = pessimisticLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stock);

    // 100 - (1 * 100) = 0
    assertEquals(0, stock.getQuantity());
  }

}
