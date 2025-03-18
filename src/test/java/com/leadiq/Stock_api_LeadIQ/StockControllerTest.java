package com.leadiq.Stock_api_LeadIQ;

import com.leadiq.Stock_api_LeadIQ.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.leadiq.Stock_api_LeadIQ.controller.StockController;

@SpringBootTest
class StockControllerTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockController stockController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchStockData() {
        doNothing().when(stockService).fetchAndStoreStockData("AAPL", "2024-01-01", "2024-01-10");
        ResponseEntity<String> response = stockController.fetchStockData("AAPL", "2024-01-01", "2024-01-10");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
