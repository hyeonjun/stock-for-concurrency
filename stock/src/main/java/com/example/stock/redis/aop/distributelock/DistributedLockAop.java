package com.example.stock.redis.aop.distributelock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

  private static final String REDISSON_AOP_LOCK_PREFIX = "redis:lock-aop:";

  private final RedissonClient redissonClient;
  private final AopForTransaction aopForTransaction;

  @Around("@annotation(com.example.stock.redis.aop.distributelock.DistributedLock)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

    String key = REDISSON_AOP_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
      methodSignature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
    RLock rLock = redissonClient.getLock(key); // key 를 사용하여 RLock 인스턴스를 가져온다.

    try {
      // waitTime 까지 락 획득 시도, leaseTime 이 지나면 락 해제
      boolean available = rLock.tryLock(
        distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
      if (!available) {
        log.info("락 획득 실패");
        return false;
      }
      log.info("DistributedLockAop - 락 획득 성공, Key: {}", key);

      // DistributedLock 어노테이션이 선언된 메서드를 별도의 트랜잭션으로 실행
      return aopForTransaction.proceed(joinPoint);
    } catch (InterruptedException e) {
      throw new InterruptedException();
    } finally {
      try {
        rLock.unlock(); // 종료 시 락 무조건 해제
      } catch (IllegalMonitorStateException e) {
        log.info("Redisson Lock Already Unlocked ServiceName: {}, Key: {}", method.getName(), key);
      }
    }
  }
}
