package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RestClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class CurrencyClientService {

    @Autowired
    private RestTemplate restTemplate;

    public List<CurrencyExchangeRateDto> getCurrencyExchangeRates(String baseCurrency, LocalDate startDate, LocalDate endDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON); // Optional for GET

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<RestClientResponse> rateResponse = restTemplate.exchange(
                String.format(
                        "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=%s&quote=USD&data_type=chart&start_date=%s&end_date=%s",
                        baseCurrency, startDate, endDate),
                HttpMethod.GET, entity,
                RestClientResponse.class);
        return rateResponse.getBody().getResponse();
    }
}
