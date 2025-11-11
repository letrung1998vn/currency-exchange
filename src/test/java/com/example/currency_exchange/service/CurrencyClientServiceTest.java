package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RestClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyClientService clientService;

    @Test
    void getCurrencyExchangeRates_returnsListFromRestTemplate() {
        CurrencyExchangeRateDto dto = new CurrencyExchangeRateDto();
        // set some fields if present; DTO is simple so leaving default is fine
        RestClientResponse resp = RestClientResponse.builder().response(List.of(dto)).build();

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(RestClientResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        List<CurrencyExchangeRateDto> result = clientService.getCurrencyExchangeRates("EUR", LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 2));

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
    }
}

