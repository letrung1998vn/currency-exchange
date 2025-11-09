package com.example.currency_exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestClientResponse {

    @JsonProperty("response")
    public List<CurrencyExchangeRateDto> response;
}
