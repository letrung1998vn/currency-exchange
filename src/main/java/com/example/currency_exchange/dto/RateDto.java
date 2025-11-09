package com.example.currency_exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private Float averageBid;

    private Float averageAsk;

    private Float highBid;

    private Float highAsk;

    private Float lowBid;

    private Float lowAsk;
}
