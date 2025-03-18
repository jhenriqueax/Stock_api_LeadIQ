package com.leadiq.Stock_api_LeadIQ.controller;

import com.leadiq.Stock_api_LeadIQ.dto.StockDTO;
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

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchStockData(@RequestParam String companySymbol,
                                                 @RequestParam String fromDate,
                                                 @RequestParam String toDate) {
        stockService.fetchAndStoreStockData(companySymbol, fromDate, toDate);
        return new ResponseEntity<>("Stock data fetched and stored successfully.", HttpStatus.OK);
    }

    @GetMapping("/{companySymbol}")
    public ResponseEntity<StockDTO> getStock(@PathVariable String companySymbol,
                                             @RequestParam String date) {
        StockDTO stockDTO = stockService.getStockBySymbolAndDate(companySymbol, date);
        return new ResponseEntity<>(stockDTO, HttpStatus.OK);
    }
}