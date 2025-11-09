package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.Float.parseFloat;

@Service
public class CurrencySyncService {

    @Autowired
    private CurrencyClientService clientService;

    @Autowired
    private CurrencyService currencyService;

    @Scheduled(cron = "0 30 17 * * *") // Runs every day at 00:30 (12:30 AM)
    public void synchCurrencyByDay() {
        String baseCurrency = "VND";
        LocalDate startDate = LocalDateTime.now().toLocalDate().minusDays(1);
        LocalDate endDate = LocalDateTime.now().toLocalDate();

        List<CurrencyExchangeRateDto> syncCurrencyRate = clientService.getCurrencyExchangeRates(baseCurrency,
                startDate, endDate);
        for (CurrencyExchangeRateDto rateDto : syncCurrencyRate) {
            RateDto rate = RateDto.builder()
                    .averageBid(parseFloat(rateDto.getAverageBid()))
                    .averageAsk(parseFloat(rateDto.getAverageAsk()))
                    .highBid(parseFloat(rateDto.getHighBid()))
                    .highAsk(parseFloat(rateDto.getHighAsk()))
                    .lowBid(parseFloat(rateDto.getLowBid()))
                    .lowAsk(parseFloat(rateDto.getLowAsk()))
                    .build();
            DateTimeFormatter OUT_FMT =
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC"));
            Instant instant = Instant.parse(rateDto.getCloseTime());

            // DTO closeTime is a string (ISO); CurrencyService expects update_time as String
            currencyService.addExchangeRate(rateDto.getBaseCurrency(),
                    OUT_FMT.format(instant), rate);
        }
        baseCurrency = "EUR";
        syncCurrencyRate = clientService.getCurrencyExchangeRates(baseCurrency,
                startDate, endDate);
        for (CurrencyExchangeRateDto rateDto : syncCurrencyRate) {
            RateDto rate = RateDto.builder()
                    .averageBid(parseFloat(rateDto.getAverageBid()))
                    .averageAsk(parseFloat(rateDto.getAverageAsk()))
                    .highBid(parseFloat(rateDto.getHighBid()))
                    .highAsk(parseFloat(rateDto.getHighAsk()))
                    .lowBid(parseFloat(rateDto.getLowBid()))
                    .lowAsk(parseFloat(rateDto.getLowAsk()))
                    .build();

            DateTimeFormatter OUT_FMT =
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC"));
            Instant instant = Instant.parse(rateDto.getCloseTime());

            // DTO closeTime is a string (ISO); CurrencyService expects update_time as String
            currencyService.addExchangeRate(rateDto.getBaseCurrency(),
                    OUT_FMT.format(instant), rate);
        }
    }

    private Double parseDouble(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Double.valueOf(s);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
