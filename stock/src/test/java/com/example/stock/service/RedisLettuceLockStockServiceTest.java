package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.stock.redis.lettuce.domain.StockRedisLettuce;
import com.example.stock.redis.lettuce.repository.StockRedisLettuceRepository;
import com.example.stock.redis.lettuce.service.LettuceLockStockService;
import com.example.stock.util.uuid.UuidProvider;
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
public class RedisLettuceLockStockServiceTest {

  @Autowired
  private LettuceLockStockService lettuceLockStockService;
  @Autowired
  private StockRedisLettuceRepository stockRedisLettuceRepository;

  private final Logger log = LoggerFactory.getLogger(RedisLettuceLockStockServiceTest.class);
  private final StockRedisLettuce initStock = new StockRedisLettuce(UuidProvider.generateUuid(), 1L, 100L);

  @BeforeEach
  public void before() {
    stockRedisLettuceRepository.save(initStock);
  }

  @AfterEach
  public void after() {
    stockRedisLettuceRepository.deleteById(initStock.getId());
  }

  @Test
  public void 동시에_재고감소_100개_요청_LettuceLock() throws InterruptedException {
    log.info("stock uuid: {}, id: {}, quantity: {}",
      initStock.getUuid(), initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          lettuceLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockRedisLettuce stockRedisLettuce = lettuceLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stockRedisLettuce);

    assertEquals(0, stockRedisLettuce.getQuantity());
  }

  @Test
  public void 동시에_재고감소_101개_요청_RedisLettuce_Lock_Exception_발생() throws InterruptedException {
    log.info("stock uuid: {}, id: {}, quantity: {}",
      initStock.getUuid(), initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          lettuceLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockRedisLettuce stockRedisLettuce = lettuceLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stockRedisLettuce);

    assertEquals(0, stockRedisLettuce.getQuantity());

    // 한번 더 요청하여 실패하는지 확인
    assertThrows(RuntimeException.class,
      () -> lettuceLockStockService.decrease(initStock.getId(), 1L));
  }

}
