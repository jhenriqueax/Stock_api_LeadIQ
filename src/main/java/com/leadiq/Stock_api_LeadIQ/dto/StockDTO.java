package com.leadiq.Stock_api_LeadIQ.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private String companySymbol;
    private LocalDate date;
    private Double openPrice;
    private Double closePrice;
    private Double highPrice;
    private Double lowPrice;
    private Long volume;
}
