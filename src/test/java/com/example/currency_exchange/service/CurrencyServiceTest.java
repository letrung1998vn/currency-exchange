package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.repo.CurrencyRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    private CurrencyRepos currencyRepos;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addExchangeRate_savesEntityWithValues() {
        RateDto rate = new RateDto();
        rate.setHighBid(1.1F);
        rate.setLowBid(0.9F);
        rate.setHighAsk(1.2F);
        rate.setLowAsk(0.8F);
        rate.setAverageAsk(1.15F);
        rate.setAverageBid(0.95F);

        currencyService.addExchangeRate("EUR", "USD", "2023-01-01T10:15:30", rate);

        ArgumentCaptor<CurrencyExchangeRate> captor = ArgumentCaptor.forClass(CurrencyExchangeRate.class);
        verify(currencyRepos, times(1)).save(captor.capture());

        CurrencyExchangeRate saved = captor.getValue();
        assertThat(saved.getBaseCurrency()).isEqualTo("EUR");
        assertThat(saved.getQuoteCurrency()).isEqualTo("USD");
        assertThat(saved.getUpdateTime()).isEqualTo(LocalDateTime.parse("2023-01-01T10:15:30"));
        assertThat(saved.getHighBid()).isEqualTo(1.1);
        assertThat(saved.getLowBid()).isEqualTo(0.9);
        assertThat(saved.getHighAsk()).isEqualTo(1.2);
        assertThat(saved.getLowAsk()).isEqualTo(0.8);
        assertThat(saved.getAverageAsk()).isEqualTo(1.15);
        assertThat(saved.getAverageBid()).isEqualTo(0.95);
    }

    @Test
    void getExchangeRate_delegatesToRepo() {
        when(currencyRepos.findByBaseCurrencyAndQuoteCurrencyOrderByBaseCurrency("A", "B"))
                .thenReturn(List.of(new CurrencyExchangeRate()));

        var res = currencyService.getExchangeRate("A", "B");
        assertThat(res).hasSize(1);
        verify(currencyRepos).findByBaseCurrencyAndQuoteCurrencyOrderByBaseCurrency("A", "B");
    }

    @Test
    void getExchangeRateAtTime_delegatesToRepo() {
        // use a valid LocalDateTime string and mock the JPQL-based repository method
        String timeStr = "2023-01-01T00:00:00";
        when(currencyRepos.findByBaseCurrencyAndQuoteCurrencyAndUpdateTimeOrderByBaseCurrency("A", "B",
                LocalDateTime.parse(timeStr)))
                .thenReturn(List.of(new CurrencyExchangeRate()));

        var res = currencyService.getExchangeRateAtTime("A", "B", timeStr);
        assertThat(res).hasSize(1);
        verify(currencyRepos).findByBaseCurrencyAndQuoteCurrencyAndUpdateTimeOrderByBaseCurrency("A",
                "B", LocalDateTime.parse(timeStr));
    }

    @Test
    void getExchangeRateByBaseCurrencyCode_delegatesToRepo() {
        String timeStr = "2023-01-01T00:00:00";
        when(currencyRepos.findByBaseCurrency("A", LocalDateTime.parse(timeStr))).thenReturn(
                List.of(new CurrencyExchangeRate()));
        var res = currencyService.getExchangeRateByBaseCurrencyCode("A", timeStr);
        assertThat(res).hasSize(1);
        verify(currencyRepos).findByBaseCurrency("A", LocalDateTime.parse(timeStr));
    }

    @Test
    void getExchangeRateByQuoteCurrencyCode_delegatesToRepo() {
        String timeStr = "2023-01-01T00:00:00";
        when(currencyRepos.findByQuoteCurrency("B", LocalDateTime.parse(timeStr))).thenReturn(
                List.of(new CurrencyExchangeRate()));
        var res = currencyService.getExchangeRateByQuoteCurrencyCode("B", timeStr);
        assertThat(res).hasSize(1);
        verify(currencyRepos).findByQuoteCurrency("B", LocalDateTime.parse(timeStr));
    }

    @Test
    void updateExchangeRate_updatesAndSaves() {
        RateDto rate = new RateDto();
        rate.setHighBid(2.0F);
        rate.setLowBid(1.0F);
        rate.setHighAsk(2.2F);
        rate.setLowAsk(0.8F);
        rate.setAverageAsk(2.1F);
        rate.setAverageBid(1.1F);

        CurrencyExchangeRate existing = new CurrencyExchangeRate();
        existing.setBaseCurrency("X");
        existing.setQuoteCurrency("Y");
        existing.setUpdateTime(LocalDateTime.parse("2023-01-01T00:00:00"));

        when(currencyRepos.findByBaseCurrencyAndQuoteCurrencyAndUpdateTime("X", "Y",
                LocalDateTime.parse("2023-01-01T00:00:00")))
                .thenReturn(existing);
        when(currencyRepos.save(existing)).thenReturn(existing);

        var res = currencyService.updateExchangeRate("X", "Y", "2023-01-01T00:00:00", rate);

        assertThat(res.getHighBid()).isEqualTo(2.0);
        assertThat(res.getLowBid()).isEqualTo(1.0);
        assertThat(res.getHighAsk()).isEqualTo(2.2);
        assertThat(res.getLowAsk()).isEqualTo(0.8);
        assertThat(res.getAverageAsk()).isEqualTo(2.1);
        assertThat(res.getAverageBid()).isEqualTo(1.1);

        verify(currencyRepos).findByBaseCurrencyAndQuoteCurrencyAndUpdateTime("X", "Y",
                LocalDateTime.parse("2023-01-01T00:00:00"));
        verify(currencyRepos).save(existing);
    }

    // Delete method tests
    @Test
    void deleteExchangeRate_delegatesToRepo() {
        currencyService.deleteExchangeRate("A", "B");
        verify(currencyRepos).deleteByBaseCurrencyAndQuoteCurrency("A", "B");
    }

    @Test
    void deleteExchangeRateAtTime_delegatesToRepo() {
        currencyService.deleteExchangeRateAtTime("X", "Y", "2023-01-02T12:00:00");
        verify(currencyRepos).deleteByBaseCurrencyAndQuoteCurrencyAndUpdateTime("X", "Y",
                LocalDateTime.parse("2023-01-02T12:00:00"));
    }

    @Test
    void deleteExchangeRateByBaseCurrencyCode_delegatesToRepo() {
        currencyService.deleteExchangeRateByBaseCurrencyCode("BASE", "2023-01-02T12:00:00");
        verify(currencyRepos).deleteByBaseCurrency("BASE", LocalDateTime.parse("2023-01-02T12:00:00"));
    }

    @Test
    void deleteExchangeRateByQuoteCurrencyCode_delegatesToRepo() {
        currencyService.deleteExchangeRateByQuoteCurrencyCode("QUOTE", "2023-01-02T12:00:00");
        verify(currencyRepos).deleteByQuoteCurrency("QUOTE", LocalDateTime.parse("2023-01-02T12:00:00"));
    }
}
