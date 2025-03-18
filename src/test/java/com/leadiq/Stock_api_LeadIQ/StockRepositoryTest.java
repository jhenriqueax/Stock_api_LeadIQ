package com.leadiq.Stock_api_LeadIQ;

import com.leadiq.Stock_api_LeadIQ.model.Stock;
import com.leadiq.Stock_api_LeadIQ.repository.StockRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void testFindByCompanySymbolAndDate() {
        Stock stock = new Stock(null, "AAPL", LocalDate.parse("2023-01-02"), 135.0, 140.0, 145.0, 130.0, 10000L);
        stockRepository.save(stock);

        Optional<Stock> foundStock = stockRepository.findByCompanySymbolAndDate("AAPL", LocalDate.parse("2023-01-02"));

        assertTrue(foundStock.isPresent());
        assertEquals("AAPL", foundStock.get().getCompanySymbol());
    }
}
