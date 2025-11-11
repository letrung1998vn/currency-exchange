package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencySyncServiceTest {

    @Mock
    private CurrencyClientService clientService;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencySyncService syncService;

    @Test
    void syncCurrencyByDay_callsAddExchangeRate_forEachDtoFromClient() {
        // prepare DTOs for VND and EUR
        CurrencyExchangeRateDto vndDto = CurrencyExchangeRateDto.builder()
                .baseCurrency("VND")
                .quoteCurrency("USD")
                .closeTime("2025-11-06T23:59:59Z")
                .averageBid("1.01")
                .averageAsk("1.02")
                .highBid("1.05")
                .highAsk("1.06")
                .lowBid("0.99")
                .lowAsk("0.98")
                .build();

        CurrencyExchangeRateDto eurDto = CurrencyExchangeRateDto.builder()
                .baseCurrency("EUR")
                .quoteCurrency("USD")
                .closeTime("2025-11-06T12:30:00Z")
                .averageBid("0.90")
                .averageAsk("0.91")
                .highBid("0.95")
                .highAsk("0.96")
                .lowBid("0.85")
                .lowAsk("0.84")
                .build();

        when(clientService.getCurrencyExchangeRates(eq("VND"), any(), any())).thenReturn(List.of(vndDto));
        when(clientService.getCurrencyExchangeRates(eq("EUR"), any(), any())).thenReturn(List.of(eurDto));

        syncService.syncCurrencyByDay();

        ArgumentCaptor<String> baseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> timeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RateDto> rateCaptor = ArgumentCaptor.forClass(RateDto.class);

        verify(currencyService, times(2)).addExchangeRate(baseCaptor.capture(), timeCaptor.capture(), rateCaptor.capture());

        List<String> bases = baseCaptor.getAllValues();
        List<String> times = timeCaptor.getAllValues();
        List<RateDto> rates = rateCaptor.getAllValues();

        assertTrue(bases.contains("VND"));
        assertTrue(bases.contains("EUR"));

        DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC"));
        String expectedVndTime = OUT_FMT.format(Instant.parse(vndDto.getCloseTime()));
        String expectedEurTime = OUT_FMT.format(Instant.parse(eurDto.getCloseTime()));
        assertTrue(times.contains(expectedVndTime));
        assertTrue(times.contains(expectedEurTime));

        RateDto vndRate = rates.stream().filter(r -> r.getAverageBid().equals(new BigDecimal("1.01"))).findFirst().orElse(null);
        assertNotNull(vndRate);
        assertEquals(new BigDecimal("1.01"), vndRate.getAverageBid());
        assertEquals(new BigDecimal("1.02"), vndRate.getAverageAsk());
        assertEquals(new BigDecimal("1.05"), vndRate.getHighBid());
        assertEquals(new BigDecimal("1.06"), vndRate.getHighAsk());
        assertEquals(new BigDecimal("0.99"), vndRate.getLowBid());
        assertEquals(new BigDecimal("0.98"), vndRate.getLowAsk());

        RateDto eurRate = rates.stream().filter(r -> r.getAverageBid().equals(new BigDecimal("0.90"))).findFirst().orElse(null);
        assertNotNull(eurRate);
        assertEquals(new BigDecimal("0.90"), eurRate.getAverageBid());
        assertEquals(new BigDecimal("0.91"), eurRate.getAverageAsk());
        assertEquals(new BigDecimal("0.95"), eurRate.getHighBid());
        assertEquals(new BigDecimal("0.96"), eurRate.getHighAsk());
        assertEquals(new BigDecimal("0.85"), eurRate.getLowBid());
        assertEquals(new BigDecimal("0.84"), eurRate.getLowAsk());
    }
}

