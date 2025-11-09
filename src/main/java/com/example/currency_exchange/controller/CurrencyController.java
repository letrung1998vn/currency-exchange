package com.example.currency_exchange.controller;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.service.CurrencyClientService;
import com.example.currency_exchange.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyClientService clientService;

    @PostMapping("/add-exchange-rate")
    public void addExchangeRate(@RequestParam String baseCurrency, @RequestParam String quoteCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME) String update_time, @RequestBody RateDto rate) {
        currencyService.addExchangeRate(baseCurrency, quoteCurrency, update_time, rate);
    }

    @GetMapping("/get-exchange-rate")
    public List<CurrencyExchangeRate> exchangeRateList(@RequestParam String baseCurrency, @RequestParam String quoteCurrency) {
        List<CurrencyExchangeRate> res = currencyService.getExchangeRate(baseCurrency, quoteCurrency);
        return res;
    }

    @GetMapping("/get-exchange-rate-at-time")
    public List<CurrencyExchangeRate> exchangeRateListAtTime(@RequestParam String baseCurrency, @RequestParam String quoteCurrency, @RequestParam String time) {
        List<CurrencyExchangeRate> res = currencyService.getExchangeRateAtTime(baseCurrency, quoteCurrency, time);
        return res;
    }

    @GetMapping("/get-exchange-rate-by-base-currency-code")
    public List<CurrencyExchangeRate> exchangeRateListByBaseCurrencyCode(@RequestParam String baseCurrency, @RequestParam String time) {
        List<CurrencyExchangeRate> res = currencyService.getExchangeRateByBaseCurrencyCode(baseCurrency, time);
        return res;

    }

    @GetMapping("/get-exchange-rate-by-quote-currency-code")
    public List<CurrencyExchangeRate> exchangeRateListByQuoteCurrencyCode(@RequestParam String quoteCurrency, @RequestParam String time) {
        List<CurrencyExchangeRate> res = currencyService.getExchangeRateByQuoteCurrencyCode(quoteCurrency, time);
        return res;
    }

    @PostMapping("/modify-exchange-rate")
    public CurrencyExchangeRate modifyExchangeRate(@RequestParam String baseCurrency, @RequestParam String quoteCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME) String update_time, @RequestBody RateDto rate) {
        CurrencyExchangeRate updated = currencyService.updateExchangeRate(baseCurrency, quoteCurrency, update_time,
                rate);
        return updated;
    }

    @GetMapping(value = "/get-fxds-exchange-rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyExchangeRateDto> getFxdsexchangeRateList(@RequestParam String baseCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE) LocalDateTime updateTime) {
        LocalDate startDate = updateTime.toLocalDate();
        LocalDate endDate = updateTime.toLocalDate().plusDays(1);

        List<CurrencyExchangeRateDto> res = clientService.getCurrencyExchangeRates(baseCurrency, startDate, endDate);
        return res;

    }

    @DeleteMapping("/delete-exchange-rate")
    public void deleteExchangeRate(@RequestParam String baseCurrency, @RequestParam String quoteCurrency) {
        currencyService.deleteExchangeRate(baseCurrency, quoteCurrency);
    }

    @DeleteMapping("/delete-exchange-rate-at-time")
    public void deleteExchangeRateAtTime(@RequestParam String baseCurrency, @RequestParam String quoteCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME) String update_time) {
        currencyService.deleteExchangeRateAtTime(baseCurrency, quoteCurrency, update_time);
    }

    @DeleteMapping("/delete-exchange-rate-by-base-currency-code")
    public void deleteExchangeRateByBaseCurrencyCode(@RequestParam String baseCurrency, @RequestParam String time) {
        currencyService.deleteExchangeRateByBaseCurrencyCode(baseCurrency, time);
    }

    @DeleteMapping("/delete-exchange-rate-by-quote-currency-code")
    public void deleteExchangeRateByQuoteCurrencyCode(@RequestParam String quoteCurrency, @RequestParam String time) {
        currencyService.deleteExchangeRateByQuoteCurrencyCode(quoteCurrency, time);
    }

}
