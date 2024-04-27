package com.example.stock.redis.repository;

import com.example.stock.redis.domain.StockRedis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRedisRepository extends JpaRepository<StockRedis, Long> {

}
