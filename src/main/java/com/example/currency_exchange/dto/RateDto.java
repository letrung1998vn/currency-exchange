package com.example.currency_exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private BigDecimal averageBid;

    private BigDecimal averageAsk;

    private BigDecimal highBid;

    private BigDecimal highAsk;

    private BigDecimal lowBid;

    private BigDecimal lowAsk;
}
