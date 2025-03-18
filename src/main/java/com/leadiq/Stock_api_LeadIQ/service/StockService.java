package com.leadiq.Stock_api_LeadIQ.service;

import com.leadiq.Stock_api_LeadIQ.exception.ApiException;
import com.leadiq.Stock_api_LeadIQ.dto.StockDTO;
import com.leadiq.Stock_api_LeadIQ.model.Stock;
import com.leadiq.Stock_api_LeadIQ.repository.StockRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.leadiq.Stock_api_LeadIQ.dto.PolygonResponseDTO;
import com.leadiq.Stock_api_LeadIQ.dto.PolygonStockDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
@Service
@EnableRetry
public class StockService {

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    //@Value("${polygon.api.key}")
    private final String polygonApiKey = "iqTo1lCuGzY_NYlZexLorbfFp6Pt56D3";

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
    }


     @Retryable(
        value = HttpClientErrorException.class, 
        maxAttempts = 7, 
        backoff = @Backoff(delay = 1000, multiplier = 2) // 1s → 2s → 4s → 8s → 16s → 32s → 64s
    )
    public void fetchAndStoreStockData(String companySymbol, String fromDate, String toDate) {
        String url = String.format(
                "https://api.polygon.io/v2/aggs/ticker/%s/range/1/day/%s/%s?apiKey=%s",
                companySymbol, fromDate, toDate, polygonApiKey);

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            String responseBody = responseEntity.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            PolygonResponseDTO polygonResponse = objectMapper.readValue(responseBody, PolygonResponseDTO.class);

            if (!"OK".equals(polygonResponse.getStatus()) || polygonResponse.getResults() == null) {
                throw new ApiException("Failed to fetch stock data from Polygon.io");
            }

            List<Stock> stocks = new ArrayList<>();
            for (PolygonStockDTO result : polygonResponse.getResults()) {
                LocalDate date = Instant.ofEpochMilli(result.getTimestamp())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();

                stocks.add(new Stock(companySymbol, date, 
                                    result.getOpenPrice(), result.getClosePrice(),
                                    result.getHighPrice(), result.getLowPrice(), result.getVolume()));
            }

            stockRepository.saveAll(stocks);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                System.out.println("API limit exceeded! Retrying...");
                throw e; 
            }
            throw new ApiException("Error fetching data from Polygon API: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException("Unexpected error: " + e.getMessage());
        }
    }

    
    public StockDTO getStockBySymbolAndDate(String companySymbol, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr.trim());
        Stock stock = stockRepository.findByCompanySymbolAndDate(companySymbol, date)
                .orElseThrow(() -> new ApiException("Stock data not found for " + companySymbol + " on " + date));

        return new StockDTO(stock.getCompanySymbol(), stock.getDate(), stock.getOpenPrice(), 
                            stock.getClosePrice(), stock.getHighPrice(), stock.getLowPrice(), stock.getVolume());
        }
    }

