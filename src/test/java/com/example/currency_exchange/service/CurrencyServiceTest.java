package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.mapper.CurrencyMapper;
import com.example.currency_exchange.repo.CurrencyRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    @Mock
    CurrencyMapper mapper;
    @Mock
    private CurrencyRepos currencyRepos;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addExchangeRate_savesEntityWithValues() {
        RateDto rate = new RateDto();
        rate.setHighBid(new BigDecimal("1.1"));
        rate.setLowBid(new BigDecimal("0.9"));
        rate.setHighAsk(new BigDecimal("1.2"));
        rate.setLowAsk(new BigDecimal("0.8"));
        rate.setAverageAsk(new BigDecimal("1.15"));
        rate.setAverageBid(new BigDecimal("0.95"));

        // ensure getExchangeRateAtTime will return empty list to allow save path
        when(currencyRepos.findByCurrencyCodeAndUpdateTime(anyString(),
                any(LocalDateTime.class)))
                .thenReturn(mock(CurrencyExchangeRate.class));

        currencyService.addExchangeRate("EUR", "2023/01/01 10:15:30", rate);

        ArgumentCaptor<CurrencyExchangeRate> captor = ArgumentCaptor.forClass(CurrencyExchangeRate.class);
        verify(currencyRepos, times(1)).save(captor.capture());

        CurrencyExchangeRate saved = captor.getValue();
        assertThat(saved.getBaseCurrency()).isEqualTo("EUR");
        assertThat(saved.getQuoteCurrency()).isEqualTo("USD");
        assertThat(saved.getUpdateTime()).isEqualTo(LocalDateTime.parse("2023/01/01 10:15:30", FMT));
        assertThat(saved.getHighBid()).isEqualTo(new BigDecimal("1.1"));
        assertThat(saved.getLowBid()).isEqualTo(new BigDecimal("0.9"));
        assertThat(saved.getHighAsk()).isEqualTo(new BigDecimal("1.2"));
        assertThat(saved.getLowAsk()).isEqualTo(new BigDecimal("0.8"));
        assertThat(saved.getAverageAsk()).isEqualTo(new BigDecimal("1.15"));
        assertThat(saved.getAverageBid()).isEqualTo(new BigDecimal("0.95"));
    }

    @Test
    void getExchangeRate_delegatesToRepo() {
        when(currencyRepos.findByCurrencyCode("A"))
                .thenReturn(List.of(new CurrencyExchangeRate()));

        var res = currencyService.getExchangeRate("A");
        assertThat(res).hasSize(1);
        verify(currencyRepos).findByCurrencyCode("A");
    }

    @Test
    void updateExchangeRate_updatesAndSaves() {
        RateDto rate = new RateDto();
        rate.setHighBid(new BigDecimal("2.0"));
        rate.setLowBid(new BigDecimal("1.0"));
        rate.setHighAsk(new BigDecimal("2.2"));
        rate.setLowAsk(new BigDecimal("0.8"));
        rate.setAverageAsk(new BigDecimal("2.1"));
        rate.setAverageBid(new BigDecimal("1.1"));

        CurrencyExchangeRate existing = new CurrencyExchangeRate();
        existing.setBaseCurrency("EUR");
        existing.setQuoteCurrency("USD");
        existing.setUpdateTime(LocalDateTime.parse("2023/01/01 00:00:00", FMT));

        when(currencyRepos.findByCurrencyCodeAndUpdateTime("EUR",
                LocalDateTime.parse("2023/01/01 00:00:00", FMT)))
                .thenReturn(existing);
        when(currencyRepos.save(existing)).thenReturn(existing);

        CurrencyExchangeRate res = currencyService.updateExchangeRate("EUR", "2023/01/01 00:00:00", rate);

        assertThat(res.getHighBid()).isEqualTo(new BigDecimal("2.0"));
        assertThat(res.getLowBid()).isEqualTo(new BigDecimal("1.0"));
        assertThat(res.getHighAsk()).isEqualTo(new BigDecimal("2.2"));
        assertThat(res.getLowAsk()).isEqualTo(new BigDecimal("0.8"));
        assertThat(res.getAverageAsk()).isEqualTo(new BigDecimal("2.1"));
        assertThat(res.getAverageBid()).isEqualTo(new BigDecimal("1.1"));

        verify(currencyRepos).findByCurrencyCodeAndUpdateTime("EUR",
                LocalDateTime.parse("2023/01/01 00:00:00", FMT));
        verify(currencyRepos).save(existing);
    }

    // Delete method tests
    @Test
    void deleteExchangeRate_delegatesToRepo() {
        currencyService.deleteExchangeRate("EUR");
        verify(currencyRepos).deleteByBaseCurrency("EUR");
    }

    @Test
    void deleteExchangeRateAtTime_delegatesToRepo() {
        currencyService.deleteExchangeRateAtTime("EUR", "2023/01/02 12:00:00");
        verify(currencyRepos).deleteByBaseCurrencyAndUpdateTime("EUR",
                LocalDateTime.parse("2023/01/02 12:00:00", FMT));
    }
}
