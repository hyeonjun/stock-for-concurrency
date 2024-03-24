package com.example.stock.synchronize.repository;

import com.example.stock.synchronize.domain.StockSync;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockSyncRepository extends JpaRepository<StockSync, Long> {

}
