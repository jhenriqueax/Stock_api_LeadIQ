package com.leadiq.Stock_api_LeadIQ.service;

import com.leadiq.Stock_api_LeadIQ.exception.ApiException;
import com.leadiq.Stock_api_LeadIQ.model.Stock;
import com.leadiq.Stock_api_LeadIQ.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    //@Value("${polygon.api.key}")
    private final String polygonApiKey = "iqTo1lCuGzY_NYlZexLorbfFp6Pt56D3";

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetch stock data from Polygon.io API and store it in the database.
     */
    public void fetchAndStoreStockData(String companySymbol, String fromDate, String toDate) {
        String url = String.format(
                "https://api.polygon.io/v2/aggs/ticker/%s/range/1/day/%s/%s?apiKey=%s",
                companySymbol, fromDate, toDate, polygonApiKey);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            if (response == null || !"OK".equals(response.get("status"))) {
                throw new ApiException("Failed to fetch stock data from Polygon.io");
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            for (Map<String, Object> result : results) {
                // Polygon returns a timestamp (in milliseconds) for the date
                Long timestamp = ((Number) result.get("t")).longValue();
                LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();

                Double openPrice = ((Number) result.get("o")).doubleValue();
                Double closePrice = ((Number) result.get("c")).doubleValue();
                Double highPrice = ((Number) result.get("h")).doubleValue();
                Double lowPrice = ((Number) result.get("l")).doubleValue();
                Long volume = ((Number) result.get("v")).longValue();

                Stock stock = new Stock(companySymbol, date, openPrice, closePrice, highPrice, lowPrice, volume);
                stockRepository.save(stock);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new ApiException("Polygon API rate limit exceeded. Please try again later.");
            }
            throw new ApiException("Error fetching data: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Retrieve stock data by company symbol and date.
     */
    public Stock getStockBySymbolAndDate(String companySymbol, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return stockRepository.findByCompanySymbolAndDate(companySymbol, date)
                .orElseThrow(() -> new ApiException("Stock data not found for " + companySymbol + " on " + date));
    }
}
