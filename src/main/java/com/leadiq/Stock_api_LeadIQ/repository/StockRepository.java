package com.leadiq.Stock_api_LeadIQ.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.leadiq.Stock_api_LeadIQ.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByCompanySymbolAndDate(String companySymbol, LocalDate date);
}
