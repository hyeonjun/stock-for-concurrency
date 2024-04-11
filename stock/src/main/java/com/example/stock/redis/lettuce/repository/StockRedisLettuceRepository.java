package com.example.stock.redis.lettuce.repository;

import com.example.stock.redis.lettuce.domain.StockRedisLettuce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRedisLettuceRepository extends JpaRepository<StockRedisLettuce, Long> {

}
