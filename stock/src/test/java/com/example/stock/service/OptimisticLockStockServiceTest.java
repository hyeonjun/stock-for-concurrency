package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.stock.domain.StockVersion;
import com.example.stock.repository.StockVersionRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
public class OptimisticLockStockServiceTest {

  @Autowired
  private StockVersionRepository stockVersionRepository;
  @Autowired
  private OptimisticLockStockService optimisticLockStockService;

  private final Logger log = LoggerFactory.getLogger(OptimisticLockStockServiceTest.class);
  private final StockVersion initStock = new StockVersion(1L, 100L);

  @BeforeEach
  public void before() {
    stockVersionRepository.save(initStock);
  }

  @AfterEach
  public void after() {
    stockVersionRepository.deleteById(initStock.getId());
  }

  @Test
  public void 동시에_재고감소_100개_요청_OptimisticLock() throws InterruptedException {
    log.info("stock id: {}, quantity: {}, version: {}",
      initStock.getId(), initStock.getQuantity(), initStock.getVersion());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          optimisticLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockVersion stockVersion = optimisticLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stockVersion);

    assertEquals(0, stockVersion.getQuantity());
  }

  @Test
  public void 동시에_재고감소_101개_요청_OptimisticLock_Exception_발생() throws InterruptedException {
    log.info("stock id: {}, quantity: {}, version: {}",
      initStock.getId(), initStock.getQuantity(), initStock.getVersion());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          optimisticLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockVersion stockVersion = optimisticLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stockVersion);

    assertEquals(0, stockVersion.getQuantity());

    // 한번 더 요청하여 실패하는지 확인
    assertThrows(RuntimeException.class,
      () -> optimisticLockStockService.decrease(initStock.getId(), 1L),
      "재고 감소 실패, 확인 바람."
      );
  }

  @Test
  public void 동시에_재고감소_1000개_요청_OptimisticLock() throws InterruptedException {
    log.info("stock id: {}, quantity: {}, version: {}",
      initStock.getId(), initStock.getQuantity(), initStock.getVersion());

    int threadCount = 1000;
    ExecutorService executorService = Executors.newFixedThreadPool(64);
    CountDownLatch latch = new CountDownLatch(threadCount);

    AtomicInteger successCount = new AtomicInteger();
    AtomicInteger failCount = new AtomicInteger();

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          optimisticLockStockService.decrease(initStock.getId(), 1L);
          successCount.getAndIncrement();
        } catch (Exception e) {
          log.error(e.getMessage());
          failCount.getAndIncrement();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executorService.shutdown();

    assertAll(
      () -> assertEquals(100, successCount.get()),
      () -> assertEquals(900, failCount.get())
    );
  }

}
