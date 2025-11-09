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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        controller.addExchangeRate("EUR", "USD", "2023-01-01T00:00:00", rate);
        verify(currencyService).addExchangeRate("EUR", "USD", "2023-01-01T00:00:00", rate);
    }

    @Test
    void exchangeRateList_delegatesToService() {
        when(currencyService.getExchangeRate("A", "B")).thenReturn(List.of(new CurrencyExchangeRate()));
        var res = controller.exchangeRateList("A", "B");
        assertThat(res).hasSize(1);
        verify(currencyService).getExchangeRate("A", "B");
    }

    @Test
    void exchangeRateListAtTime_delegatesToService() {
        when(currencyService.getExchangeRateAtTime("A", "B", "t")).thenReturn(List.of(new CurrencyExchangeRate()));
        var res = controller.exchangeRateListAtTime("A", "B", "t");
        assertThat(res).hasSize(1);
        verify(currencyService).getExchangeRateAtTime("A", "B", "t");
    }

    @Test
    void exchangeRateListByBaseCurrencyCode_delegatesToService() {
        when(currencyService.getExchangeRateByBaseCurrencyCode("A", "2023-01-01T00:00:00")).thenReturn(
                List.of(new CurrencyExchangeRate()));
        var res = controller.exchangeRateListByBaseCurrencyCode("A", "2023-01-01T00:00:00");
        assertThat(res).hasSize(1);
        verify(currencyService).getExchangeRateByBaseCurrencyCode("A", "2023-01-01T00:00:00");
    }

    @Test
    void exchangeRateListByQuoteCurrencyCode_delegatesToService() {
        when(currencyService.getExchangeRateByQuoteCurrencyCode("B", "2023-01-01T00:00:00")).thenReturn(
                List.of(new CurrencyExchangeRate()));
        var res = controller.exchangeRateListByQuoteCurrencyCode("B", "2023-01-01T00:00:00");
        assertThat(res).hasSize(1);
        verify(currencyService).getExchangeRateByQuoteCurrencyCode("B", "2023-01-01T00:00:00");
    }

    @Test
    void modifyExchangeRate_delegatesAndReturns() {
        CurrencyExchangeRate entity = new CurrencyExchangeRate();
        when(currencyService.updateExchangeRate(eq("X"), eq("Y"), eq("2023-01-01T00:00:00"), any(RateDto.class)))
                .thenReturn(entity);
        var res = controller.modifyExchangeRate("X", "Y", "2023-01-01T00:00:00", new RateDto());
        assertThat(res).isSameAs(entity);
        verify(currencyService).updateExchangeRate(eq("X"), eq("Y"), eq("2023-01-01T00:00:00"), any(RateDto.class));
    }

    @Test
    void getFxdsexchangeRateList_delegatesToClientService() {
        var dto = new CurrencyExchangeRateDto();
        when(clientService.getCurrencyExchangeRates(eq("EUR"), any(), any())).thenReturn(List.of(dto));

        var res = controller.getFxdsexchangeRateList("EUR", LocalDateTime.of(2023, 1, 1, 0, 0));
        assertThat(res).hasSize(1);
        assertThat(res.get(0)).isSameAs(dto);
        verify(clientService).getCurrencyExchangeRates(eq("EUR"), any(), any());
    }

    // New tests for delete endpoints
    @Test
    void deleteExchangeRate_delegatesToService() {
        controller.deleteExchangeRate("A", "B");
        verify(currencyService).deleteExchangeRate("A", "B");
    }

    @Test
    void deleteExchangeRateAtTime_delegatesToService() {
        String time = "2023-01-01T12:00:00";
        controller.deleteExchangeRateAtTime("A", "B", time);
        verify(currencyService).deleteExchangeRateAtTime("A", "B", time);
    }

    @Test
    void deleteExchangeRateByBaseCurrencyCode_delegatesToService() {
        controller.deleteExchangeRateByBaseCurrencyCode("EUR", "2023-01-01T00:00:00");
        verify(currencyService).deleteExchangeRateByBaseCurrencyCode("EUR", "2023-01-01T00:00:00");
    }

    @Test
    void deleteExchangeRateByQuoteCurrencyCode_delegatesToService() {
        controller.deleteExchangeRateByQuoteCurrencyCode("USD", "2023-01-01T00:00:00");
        verify(currencyService).deleteExchangeRateByQuoteCurrencyCode("USD","2023-01-01T00:00:00");
    }
}
