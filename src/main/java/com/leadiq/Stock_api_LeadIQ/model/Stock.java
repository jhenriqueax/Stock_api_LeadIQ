package com.leadiq.Stock_api_LeadIQ.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
public class Stock {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private byte[] id;

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

    public Stock(byte[] id, String companySymbol, LocalDate date, Double openPrice, Double closePrice, Double highPrice, Double lowPrice, Long volume) {
        this.id = id;
        this.companySymbol = companySymbol;
        this.date = date;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
    }

    public static byte[] generateShortHash(String companySymbol, LocalDate date) {
        try {
            String input = companySymbol + date.toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Retorna apenas os primeiros 16 bytes do hash para economizar espaço
            byte[] shortHash = new byte[16];
            System.arraycopy(hash, 0, shortHash, 0, 16);
            return shortHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash para ID", e);
        }
    }
}
