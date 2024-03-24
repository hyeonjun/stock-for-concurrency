package com.example.stock.pessimistic.repository;

import com.example.stock.pessimistic.domain.StockPessimistic;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockPessimisticRepository extends JpaRepository<StockPessimistic, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT sp FROM StockPessimistic sp WHERE sp.id = :id")
  Optional<StockPessimistic> findByIdForUpdate(long id);

}
