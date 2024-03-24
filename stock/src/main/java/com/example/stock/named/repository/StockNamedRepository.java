package com.example.stock.named.repository;

import com.example.stock.named.domain.StockNamed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockNamedRepository extends JpaRepository<StockNamed, Long> {

  @Query(value = "SELECT get_lock(:uuid, 3000)", nativeQuery = true)
  void getLock(String uuid);

  @Query(value = "SELECT release_lock(:uuid)", nativeQuery = true)
  void releaseLock(String uuid);
}
