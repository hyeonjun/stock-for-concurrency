package com.example.stock.redis.aop.distributelock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {

  // AOP 에서 트랜잭션 분리를 위한 클래스
  // 트랜잭션 유무에 관계없이 별도의 트랜잭션으로 동작하게 설정
  // 반드시 드랜백션 커밋 이후 락이 해제되게끔 처리
  // 이는 동시성 환경에서 데이터 정합성을 보장하기 위해서이다.

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
    return joinPoint.proceed();
  }
}
