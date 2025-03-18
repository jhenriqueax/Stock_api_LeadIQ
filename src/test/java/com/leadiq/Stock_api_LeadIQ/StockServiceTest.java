package com.leadiq.Stock_api_LeadIQ;

import com.leadiq.Stock_api_LeadIQ.dto.StockDTO;
import com.leadiq.Stock_api_LeadIQ.exception.ApiException;
import com.leadiq.Stock_api_LeadIQ.model.Stock;
import com.leadiq.Stock_api_LeadIQ.repository.StockRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.leadiq.Stock_api_LeadIQ.service.StockService;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StockService stockService;

    @Test
    void testGetStockBySymbolAndDate_Found() {
        String companySymbol = "AAPL";
        String dateStr = "2023-01-02";
        LocalDate date = LocalDate.parse(dateStr);
        Stock stock = new Stock(1L, companySymbol, date, 135.0, 140.0, 145.0, 130.0, 10000L);

        when(stockRepository.findByCompanySymbolAndDate(companySymbol, date)).thenReturn(Optional.of(stock));

        StockDTO stockDTO = stockService.getStockBySymbolAndDate(companySymbol, dateStr);

        assertNotNull(stockDTO);
        assertEquals(companySymbol, stockDTO.getCompanySymbol());
        assertEquals(date, stockDTO.getDate());
    }

    @Test
    void testGetStockBySymbolAndDate_NotFound() {
        String companySymbol = "AAPL";
        String dateStr = "2023-01-02";
        LocalDate date = LocalDate.parse(dateStr);

        when(stockRepository.findByCompanySymbolAndDate(companySymbol, date)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> stockService.getStockBySymbolAndDate(companySymbol, dateStr));
    }
}
