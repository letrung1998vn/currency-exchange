package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.mapper.CurrencyMapper;
import com.example.currency_exchange.repo.CurrencyRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final String TIME = "2025/01/02 03:04:05";

    @Mock
    CurrencyRepos currencyRepos;
    @Mock
    MessageSource messageSource;
    @Mock
    CurrencyMapper mapper;

    @InjectMocks
    CurrencyService currencyService;

    @BeforeEach
    void setup() {
        lenient().when(messageSource.getMessage(anyString(), any(), any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void addExchangeRate_whenNoExisting_shouldSaveEntity() {
        when(mapper.toDto(any())).thenReturn(null);

        RateDto rate = mock(RateDto.class);
        when(rate.getHighBid()).thenReturn(BigDecimal.valueOf(1.1));
        when(rate.getLowBid()).thenReturn(BigDecimal.valueOf(1.0));
        when(rate.getHighAsk()).thenReturn(BigDecimal.valueOf(1.2));
        when(rate.getLowAsk()).thenReturn(BigDecimal.valueOf(0.9));
        when(rate.getAverageAsk()).thenReturn(BigDecimal.valueOf(1.15));
        when(rate.getAverageBid()).thenReturn(BigDecimal.valueOf(1.05));

        currencyService.addExchangeRate("EUR", TIME, rate);

        ArgumentCaptor<CurrencyExchangeRate> captor = ArgumentCaptor.forClass(CurrencyExchangeRate.class);
        verify(currencyRepos, times(1)).save(captor.capture());
        CurrencyExchangeRate saved = captor.getValue();
        assertEquals("EUR", saved.getBaseCurrency());
        assertEquals("USD", saved.getQuoteCurrency());
        assertEquals(LocalDateTime.parse(TIME, FMT), saved.getUpdateTime());
        assertEquals(BigDecimal.valueOf(1.1), saved.getHighBid());
        assertEquals(BigDecimal.valueOf(1.0), saved.getLowBid());
        assertEquals(BigDecimal.valueOf(1.2), saved.getHighAsk());
        assertEquals(BigDecimal.valueOf(0.9), saved.getLowAsk());
        assertEquals(BigDecimal.valueOf(1.15), saved.getAverageAsk());
        assertEquals(BigDecimal.valueOf(1.05), saved.getAverageBid());
    }

    @Test
    void addExchangeRate_whenExisting_shouldThrow() {
        when(mapper.toDto(any())).thenReturn(new CurrencyExchangeRateDto());
        when(messageSource.getMessage(eq("insertMutlipleError"), any(), any())).thenReturn("err");

        RateDto rate = mock(RateDto.class);

        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> currencyService.addExchangeRate("EUR", TIME, rate));
        assertEquals("err", ex.getMessage());
        verify(currencyRepos, never()).save(any());
    }

    @Test
    void getExchangeRate_whenFound_returnsMappedList() {
        CurrencyExchangeRate entity = new CurrencyExchangeRate();
        List<CurrencyExchangeRate> repoList = List.of(entity);
        when(currencyRepos.findByCurrencyCode("EUR")).thenReturn(repoList);

        CurrencyExchangeRateDto dto = new CurrencyExchangeRateDto();
        when(mapper.toDto(entity)).thenReturn(dto);

        List<CurrencyExchangeRateDto> result = currencyService.getExchangeRate("EUR");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void getExchangeRate_whenNotFound_throws() {
        when(currencyRepos.findByCurrencyCode("EUR")).thenReturn(Collections.emptyList());
        when(messageSource.getMessage(eq("currencyCodeNotFound"), any(), any())).thenReturn("not found");

        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> currencyService.getExchangeRate("EUR"));
        assertEquals("not found", ex.getMessage());
    }

    @Test
    void getExchangeRateAtTime_whenEntityPresent_returnsDto() {
        CurrencyExchangeRate entity = new CurrencyExchangeRate();
        when(currencyRepos.findByCurrencyCodeAndUpdateTime(eq("EUR"), any(LocalDateTime.class))).thenReturn(entity);

        CurrencyExchangeRateDto dto = new CurrencyExchangeRateDto();
        when(mapper.toDto(entity)).thenReturn(dto);

        CurrencyExchangeRateDto result = currencyService.getExchangeRateAtTime("EUR", TIME);
        assertSame(dto, result);
    }

    @Test
    void getExchangeRateAtTime_whenEntityNull_mapperCalledWithNull_returnsNull() {
        when(currencyRepos.findByCurrencyCodeAndUpdateTime(eq("EUR"), any(LocalDateTime.class))).thenReturn(null);
        when(mapper.toDto(null)).thenReturn(null);

        CurrencyExchangeRateDto result = currencyService.getExchangeRateAtTime("EUR", TIME);
        assertNull(result);
    }

    @Test
    void updateExchangeRate_whenFound_updatesAndSaves() {
        CurrencyExchangeRate entity = new CurrencyExchangeRate();
        when(currencyRepos.findByCurrencyCodeAndUpdateTime(eq("EUR"), any(LocalDateTime.class))).thenReturn(entity);

        RateDto rate = mock(RateDto.class);
        when(rate.getHighBid()).thenReturn(BigDecimal.valueOf(2.1));
        when(rate.getLowBid()).thenReturn(BigDecimal.valueOf(2.0));
        when(rate.getHighAsk()).thenReturn(BigDecimal.valueOf(2.2));
        when(rate.getLowAsk()).thenReturn(BigDecimal.valueOf(1.9));
        when(rate.getAverageAsk()).thenReturn(BigDecimal.valueOf(2.15));
        when(rate.getAverageBid()).thenReturn(BigDecimal.valueOf(2.05));

        CurrencyExchangeRate saved = new CurrencyExchangeRate();
        when(currencyRepos.save(entity)).thenReturn(saved);

        CurrencyExchangeRate result = currencyService.updateExchangeRate("EUR", TIME, rate);
        assertSame(saved, result);
        verify(currencyRepos).save(entity);
        assertEquals(BigDecimal.valueOf(2.1), entity.getHighBid());
        assertEquals(BigDecimal.valueOf(2.0), entity.getLowBid());
    }

    @Test
    void updateExchangeRate_whenNotFound_throws() {
        when(currencyRepos.findByCurrencyCodeAndUpdateTime(eq("EUR"), any(LocalDateTime.class))).thenReturn(null);
        when(messageSource.getMessage(eq("updateCurrencyExchangeNotFound"), any(), any())).thenReturn("missing");
        RateDto rate = mock(RateDto.class);

        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> currencyService.updateExchangeRate("EUR", TIME, rate));
        assertEquals("missing", ex.getMessage());
        verify(currencyRepos, never()).save(any());
    }

    @Test
    void deleteExchangeRate_callsRepo() {
        currencyService.deleteExchangeRate("EUR");
        verify(currencyRepos).deleteByBaseCurrency("EUR");
    }

    @Test
    void deleteExchangeRateAtTime_callsRepoWithParsedTime() {
        currencyService.deleteExchangeRateAtTime("EUR", TIME);
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(currencyRepos).deleteByBaseCurrencyAndUpdateTime(eq("EUR"), captor.capture());
        assertEquals(LocalDateTime.parse(TIME, FMT), captor.getValue());
    }
}
