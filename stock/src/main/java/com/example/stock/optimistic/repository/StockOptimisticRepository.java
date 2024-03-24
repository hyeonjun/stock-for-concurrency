package com.example.stock.optimistic.repository;

import com.example.stock.optimistic.domain.StockOptimistic;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockOptimisticRepository extends JpaRepository<StockOptimistic, Long> {

  @Lock(LockModeType.OPTIMISTIC)
  @Query("SELECT so FROM StockOptimistic so WHERE so.id = :id")
  Optional<StockOptimistic> findByIdLock(long id);

}
