package com.leadiq.Stock_api_LeadIQ.controller;

import com.leadiq.Stock_api_LeadIQ.model.Stock;
import com.leadiq.Stock_api_LeadIQ.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Endpoint to fetch stock data from Polygon.io and store in database.
     * Example request: POST /stocks/fetch?companySymbol=AAPL&fromDate=2025-03-01&toDate=2025-03-05
     */
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchStockData(@RequestParam String companySymbol,
                                                 @RequestParam String fromDate,
                                                 @RequestParam String toDate) {
        stockService.fetchAndStoreStockData(companySymbol, fromDate, toDate);
        return new ResponseEntity<>("Stock data fetched and stored successfully.", HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve stored stock data by company symbol and date.
     * Example request: GET /stocks/AAPL?date=2025-03-01
     */
    @GetMapping("/{companySymbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String companySymbol,
                                          @RequestParam String date) {
        Stock stock = stockService.getStockBySymbolAndDate(companySymbol, date);
        return new ResponseEntity<>(stock, HttpStatus.OK);
    }
}