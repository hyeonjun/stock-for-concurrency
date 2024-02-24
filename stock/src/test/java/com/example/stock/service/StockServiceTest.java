package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class StockServiceTest {

  @Autowired
  private StockService stockService;
  @Autowired
  private StockRepository stockRepository;

  private Logger log = LoggerFactory.getLogger(StockServiceTest.class);

  @BeforeEach
  public void before() {
    stockRepository.save(new Stock(1L, 100L));
  }

  @Test
  public void 재고감소() {
    stockService.decrease(1L, 1L);

    Stock stock = stockService.getStock(1L);

    log.info("{}", stock);

    assertEquals(99, stock.getQuantity());
  }
}
