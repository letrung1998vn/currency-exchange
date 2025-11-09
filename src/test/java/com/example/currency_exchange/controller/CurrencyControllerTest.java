package com.example.currency_exchange.controller;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.service.CurrencyClientService;
import com.example.currency_exchange.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private CurrencyClientService clientService;

    @InjectMocks
    private CurrencyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addExchangeRate_delegatesToService() {
        RateDto rate = new RateDto();
        rate.setAverageBid(1.23f);

        controller.addExchangeRate("EUR", "2025/11/01 00:00:00", rate);

        verify(currencyService, times(1)).addExchangeRate(eq("EUR"), eq("2025/11/01 00:00:00"), eq(rate));
    }

    @Test
    void exchangeRateList_returnsServiceResult() {
        CurrencyExchangeRate e = new CurrencyExchangeRate();
        e.setBaseCurrency("EUR");
        e.setQuoteCurrency("USD");
        List<CurrencyExchangeRate> expected = Collections.singletonList(e);

        when(currencyService.getExchangeRate("EUR")).thenReturn(expected);

        List<CurrencyExchangeRate> actual = controller.exchangeRateList("EUR");

        assertSame(expected, actual);
        verify(currencyService, times(1)).getExchangeRate("EUR");
    }

    @Test
    void exchangeRateListAtTime_returnsServiceResult() {
        CurrencyExchangeRate e = new CurrencyExchangeRate();
        e.setBaseCurrency("EUR");
        e.setQuoteCurrency("USD");
        List<CurrencyExchangeRate> expected = Collections.singletonList(e);

        when(currencyService.getExchangeRateAtTime("EUR", "2025/11/01 00:00:00")).thenReturn(expected);

        List<CurrencyExchangeRate> actual = controller.exchangeRateListAtTime("EUR", "2025/11/01 00:00:00");

        assertEquals(1, actual.size());
        // expected baseCurrency is EUR and quoteCurrency is USD as set above
        assertEquals("EUR", actual.get(0).getBaseCurrency());
        assertEquals("USD", actual.get(0).getQuoteCurrency());
        verify(currencyService, times(1)).getExchangeRateAtTime("EUR", "2025/11/01 00:00:00");
    }

    @Test
    void exchangeRateListByBaseCurrencyCode_returnsServiceResult() {
        CurrencyExchangeRate e = new CurrencyExchangeRate();
        e.setBaseCurrency("USD");
        List<CurrencyExchangeRate> expected = Collections.singletonList(e);

        when(currencyService.getExchangeRateByBaseCurrencyCode("USD", "2025/11/01 00:00:00")).thenReturn(expected);

        List<CurrencyExchangeRate> actual = controller.exchangeRateListByBaseCurrencyCode("USD", "2025/11/01 00:00:00");

        assertFalse(actual.isEmpty());
        assertEquals("USD", actual.get(0).getBaseCurrency());
        verify(currencyService, times(1)).getExchangeRateByBaseCurrencyCode("USD", "2025/11/01 00:00:00");
    }

    @Test
    void modifyExchangeRate_returnsUpdatedEntity() {
        RateDto rate = new RateDto();
        rate.setAverageBid(2.5f);
        CurrencyExchangeRate updated = new CurrencyExchangeRate();
        updated.setBaseCurrency("USD");
        updated.setQuoteCurrency("JPY");

        when(currencyService.updateExchangeRate("USD", "2025/11/01 00:00:00", rate)).thenReturn(updated);

        CurrencyExchangeRate result = controller.modifyExchangeRate("USD", "2025/11/01 00:00:00", rate);

        assertNotNull(result);
        assertEquals("USD", result.getBaseCurrency());
        assertEquals("JPY", result.getQuoteCurrency());
        verify(currencyService, times(1)).updateExchangeRate("USD", "2025/11/01 00:00:00", rate);
    }

    @Test
    void getFxdsexchangeRateList_delegatesToClientService() {
        CurrencyExchangeRateDto dto = new CurrencyExchangeRateDto();
        dto.setBaseCurrency("USD");
        List<CurrencyExchangeRateDto> expected = Collections.singletonList(dto);

        LocalDateTime updateTime = LocalDate.of(2025, 11, 1).atStartOfDay();
        LocalDate startDate = updateTime.toLocalDate();
        LocalDate endDate = updateTime.toLocalDate().plusDays(1);

        when(clientService.getCurrencyExchangeRates("USD", startDate, endDate)).thenReturn(expected);

        List<CurrencyExchangeRateDto> actual = controller.getFxdsexchangeRateList("USD", updateTime);

        assertSame(expected, actual);
        verify(clientService, times(1)).getCurrencyExchangeRates("USD", startDate, endDate);
    }

    @Test
    void deleteExchangeRate_callsService() {
        doNothing().when(currencyService).deleteExchangeRate("EUR");

        controller.deleteExchangeRate("EUR");

        verify(currencyService, times(1)).deleteExchangeRate("EUR");
    }

    @Test
    void deleteExchangeRateAtTime_callsService() {
        doNothing().when(currencyService).deleteExchangeRateAtTime("EUR", "2025/11/01 00:00:00");

        controller.deleteExchangeRateAtTime("EUR", "2025/11/01 00:00:00");

        verify(currencyService, times(1)).deleteExchangeRateAtTime("EUR", "2025/11/01 00:00:00");
    }
}
