package com.leadiq.Stock_api_LeadIQ.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolygonResponseDTO {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("results")
    private List<PolygonStockDTO> results;

}
