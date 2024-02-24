package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
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
public class StockServiceTest {

  @Autowired
  private StockService stockService;
  @Autowired
  private StockRepository stockRepository;

  private final Logger log = LoggerFactory.getLogger(StockServiceTest.class);
  private final Stock initStock = new Stock(1L, 100L);

  @BeforeEach
  public void before() {
    stockRepository.save(initStock);
  }

  @AfterEach
  public void after() {
    stockRepository.delete(initStock);
  }

  @Test
  public void 재고감소() {
    log.info("stock id: {}, quantity: {}", initStock.getId(), initStock.getQuantity());
    stockService.decrease(initStock.getId(), 1L);

    Stock stock = stockService.getStock(initStock.getId());
    log.info("{}", stock);

    assertEquals(99, stock.getQuantity());
  }

  @Test
  public void 동시에_재고감소_100개_요청() throws InterruptedException {
    log.info("stock id: {}, quantity: {}", initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    // 멀티스레드 이용 ExecutorService: 비동기를 단순하게 처리할 수 있도록 해주는 Java API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 다른 스레드에서 수행이 완료될 때까지 대기할 수 있도록 도와주는 API - 요청이 끝날 때까지 기다림
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          stockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage()); // 재고를 찾지 못하는 RuntimeException 발생
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    Stock stock = stockService.getStock(initStock.getId());
    log.info("stock: {}", stock);

    // 100 - (1 * 100) = 0
    assertNotEquals(0, stock.getQuantity());
  }

  @Test
  public void 동시에_재고감소_100개_요청_Synchronized() throws InterruptedException {
    log.info("stock id: {}, quantity: {}", initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    // 멀티스레드 이용 ExecutorService: 비동기를 단순하게 처리할 수 있도록 해주는 Java API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 다른 스레드에서 수행이 완료될 때까지 대기할 수 있도록 도와주는 API - 요청이 끝날 때까지 기다림
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          stockService.decreaseSynchronized(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage()); // 재고를 찾지 못하는 RuntimeException 발생
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    Stock stock = stockService.getStock(initStock.getId());
    log.info("stock: {}", stock);

    // 100 - (1 * 100) = 0
    assertEquals(0, stock.getQuantity());
  }
}
