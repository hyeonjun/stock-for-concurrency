package com.example.stock.repository;

import com.example.stock.domain.StockVersion;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockVersionRepository extends JpaRepository<StockVersion, Long> {

  @Lock(LockModeType.OPTIMISTIC)
  @Query("SELECT sv FROM StockVersion sv WHERE sv.id = :id")
  Optional<StockVersion> findByIdLock(long id);

}
