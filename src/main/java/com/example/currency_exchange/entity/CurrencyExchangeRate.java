package com.example.currency_exchange.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_exchange_rate")
@Data
public class CurrencyExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "base_currency")
    private String baseCurrency;

    @Column(name = "quote_currency")
    private String quoteCurrency;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "average_bid")
    private BigDecimal averageBid;

    @Column(name = "average_ask")
    private BigDecimal averageAsk;

    @Column(name = "high_bid")
    private BigDecimal highBid;

    @Column(name = "high_ask")
    private BigDecimal highAsk;

    @Column(name = "low_bid")
    private BigDecimal lowBid;

    @Column(name = "low_ask")
    private BigDecimal lowAsk;

}
