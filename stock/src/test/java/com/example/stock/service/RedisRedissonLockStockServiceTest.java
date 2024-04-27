package com.example.stock.service;

import com.example.stock.redis.domain.StockRedis;
import com.example.stock.redis.repository.StockRedisRepository;
import com.example.stock.redis.service.redisson.RedissonLockStockService;
import com.example.stock.util.uuid.UuidProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Rollback(value = false)
public class RedisRedissonLockStockServiceTest {

  @Autowired
  private RedissonLockStockService redissonLockStockService;
  @Autowired
  private StockRedisRepository stockRedisRedissonRepository;

  private final Logger log = LoggerFactory.getLogger(RedisRedissonLockStockServiceTest.class);
  private final StockRedis initStock = new StockRedis(UuidProvider.generateUuid(), 1L, 100L);

  @BeforeEach
  public void before() {
    stockRedisRedissonRepository.save(initStock);
  }

  @AfterEach
  public void after() {
    stockRedisRedissonRepository.deleteById(initStock.getId());
  }

  @Test
  public void 동시에_재고감소_100개_요청_RedissonLock() throws InterruptedException {
    log.info("stock uuid: {}, id: {}, quantity: {}",
      initStock.getUuid(), initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          redissonLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockRedis stock = redissonLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stock);

    assertEquals(0, stock.getQuantity());
  }

  @Test
  public void 동시에_재고감소_101개_요청_RedisRedisson_Lock_Exception_발생() throws InterruptedException {
    log.info("stock uuid: {}, id: {}, quantity: {}",
      initStock.getUuid(), initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          redissonLockStockService.decrease(initStock.getId(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    StockRedis stock = redissonLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stock);

    assertEquals(0, stock.getQuantity());

    // 한번 더 요청하여 실패하는지 확인
    assertThrows(RuntimeException.class,
      () -> redissonLockStockService.decrease(initStock.getId(), 1L));
  }
}
