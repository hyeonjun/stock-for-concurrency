package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stock.named.domain.StockNamed;
import com.example.stock.named.repository.StockNamedRepository;
import com.example.stock.named.service.NamedLockStockService;
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
public class NamedLockStockServiceTest {

  @Autowired
  private StockNamedRepository stockNamedRepository;
  @Autowired
  private NamedLockStockService namedLockStockService;

  private final Logger log = LoggerFactory.getLogger(NamedLockStockServiceTest.class);
  private final StockNamed initStock = new StockNamed(UuidProvider.generateUuid(), 1L, 100L);

  @BeforeEach
  public void before() {
    stockNamedRepository.save(initStock);
  }

  @AfterEach
  public void after() {
    stockNamedRepository.deleteById(initStock.getId());
  }

  @Test
  public void 동시에_재고감소_100개_요청_NamedLock() throws InterruptedException{
    log.info("stock uuid: {}, id: {}, quantity: {}",
      initStock.getUuid(), initStock.getId(), initStock.getQuantity());

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          namedLockStockService.decrease(initStock.getId(), initStock.getUuid(), 1L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();;
        }
      });
    }

    latch.await();

    StockNamed stock = namedLockStockService.getStock(initStock.getId());
    log.info("stock: {}", stock);

    assertEquals(0, stock.getQuantity());
  }

}
