package com.example.stock.repository;

import com.example.stock.domain.Stock;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Stock s WHERE s.id = :id")
  Optional<Stock> findByIdForUpdate(long id);

}
