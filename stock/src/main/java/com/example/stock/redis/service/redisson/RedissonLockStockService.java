package com.example.stock.redis.service.redisson;

import com.example.stock.redis.domain.StockRedis;
import com.example.stock.redis.helper.RedisLockStockHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockStockService {

  private final RedissonClient redissonClient;
  private final RedisLockStockHelper redisLockStockHelper;

  @Transactional(readOnly = true)
  public StockRedis getStock(Long id) {
    return redisLockStockHelper.getStock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));
  }

  /**
   * Lock 획득 재시도를 기본으로 제공한다.
   * Pub-Sub 방식으로 구현되어 있기 때문에 Lettuce와 비교했을 때 부하가 덜 발생한다.
   * 별도의 라이브러리를 사용해야 한다.
   * Lock을 라이브러리 차원에서 제공해주기 때문에 사용법을 공부해야 한다.
   * 실두에서는 재시도가 필요한 Lock의 경우 Redisson을 활용해야 하고, 그렇지 않은 경우 Lettuce를 활용한다.
   * 재시도가 필요한 경우: 선착순 100명까지 물품 구매할 수 있는 경우
   * 재시도가 필요하지 않은 경우: 선착순 한명한 가능, Lock 획득 재시도 불필요
   */
  public void decrease(Long id, Long quantity) {
    StockRedis stock = redisLockStockHelper
      .getStock(id)
      .orElseThrow(() -> new RuntimeException("재고가 없습니다."));

    RLock lock = redissonClient.getLock(stock.getUuid());

    try {
      // 몇 초동안 락 획득을 할 것인지, 그리고 몇 초동안 점유할 것인지 설정
      boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
      if (!available) {
        log.info("락 획득 실패");
        return;
      }

      redisLockStockHelper.decrease(id, quantity);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

}
