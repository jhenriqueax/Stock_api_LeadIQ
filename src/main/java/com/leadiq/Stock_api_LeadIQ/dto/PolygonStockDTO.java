package com.leadiq.Stock_api_LeadIQ.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolygonStockDTO {

    @JsonProperty("t")
    private Long timestamp;

    @JsonProperty("o")
    private Double openPrice;

    @JsonProperty("c")
    private Double closePrice;

    @JsonProperty("h")
    private Double highPrice;

    @JsonProperty("l")
    private Double lowPrice;

    @JsonProperty("v")
    private Long volume;
}
