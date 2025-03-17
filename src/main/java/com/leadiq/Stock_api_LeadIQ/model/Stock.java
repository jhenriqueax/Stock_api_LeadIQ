package com.leadiq.Stock_api_LeadIQ.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_symbol", nullable = false)
    private String companySymbol;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "open_price")
    private Double openPrice;
    
    @Column(name = "close_price")
    private Double closePrice;
    
    @Column(name = "high_price")
    private Double highPrice;
    
    @Column(name = "low_price")
    private Double lowPrice;
    
    @Column(name = "volume")
    private Long volume;

    public Stock(String companySymbol, LocalDate date, Double openPrice, Double closePrice, Double highPrice, Double lowPrice, Long volume) {
        this.companySymbol = companySymbol;
        this.date = date;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
    }
}
