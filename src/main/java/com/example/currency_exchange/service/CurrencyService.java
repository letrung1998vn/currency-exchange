package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.mapper.CurrencyMapper;
import com.example.currency_exchange.repo.CurrencyRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    @Autowired
    CurrencyRepos currencyRepos;
    @Autowired
    MessageSource messageSource;
    @Autowired
    CurrencyMapper mapper;

    private static CurrencyExchangeRate getCurrencyExchangeRate(String baseCurrency, String update_time, RateDto rate) {
        CurrencyExchangeRate rateEntity = new CurrencyExchangeRate();
        rateEntity.setBaseCurrency(baseCurrency);
        rateEntity.setQuoteCurrency("USD");
        rateEntity.setUpdateTime(LocalDateTime.parse(update_time, FMT));
        rateEntity.setHighBid(rate.getHighBid());
        rateEntity.setLowBid(rate.getLowBid());
        rateEntity.setHighAsk(rate.getHighAsk());
        rateEntity.setLowAsk(rate.getLowAsk());
        rateEntity.setAverageAsk(rate.getAverageAsk());
        rateEntity.setAverageBid(rate.getAverageBid());
        return rateEntity;
    }

    public void addExchangeRate(String baseCurrency, String update_time, RateDto rate) {
        CurrencyExchangeRateDto existingRates = getExchangeRateAtTime(
                baseCurrency, update_time);
        if (existingRates != null) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("insertMutlipleError", null, LocaleContextHolder.getLocale()));
        }

        CurrencyExchangeRate rateEntity = getCurrencyExchangeRate(baseCurrency,
                update_time, rate);
        currencyRepos.save(rateEntity);
    }

    public List<CurrencyExchangeRateDto> getExchangeRate(String baseCurrency) {
        List<CurrencyExchangeRate> result = currencyRepos.findByCurrencyCode(
                baseCurrency);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCodeNotFound", null, LocaleContextHolder.getLocale()));
        }
        List<CurrencyExchangeRateDto> currencyExchangeRateDtos = new ArrayList<>();
        for (CurrencyExchangeRate exchangeRate : result) {
            currencyExchangeRateDtos.add(mapper.toDto(exchangeRate));
        }
        return currencyExchangeRateDtos;
    }

    public CurrencyExchangeRateDto getExchangeRateAtTime(String baseCurrency, String time) {
        CurrencyExchangeRate rateEntity = currencyRepos.findByCurrencyCodeAndUpdateTime(baseCurrency,
                LocalDateTime.parse(time, FMT));
        return mapper.toDto(rateEntity);
    }

    public CurrencyExchangeRate updateExchangeRate(String baseCurrency, String update_time, RateDto rate) {
        CurrencyExchangeRate rateEntity = currencyRepos.findByCurrencyCodeAndUpdateTime(baseCurrency,
                LocalDateTime.parse(update_time, FMT));
        if (rateEntity == null) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("updateCurrencyExchangeNotFound", null, LocaleContextHolder.getLocale()));
        }
        rateEntity.setHighBid(rate.getHighBid());
        rateEntity.setLowBid(rate.getLowBid());
        rateEntity.setHighAsk(rate.getHighAsk());
        rateEntity.setLowAsk(rate.getLowAsk());
        rateEntity.setAverageAsk(rate.getAverageAsk());
        rateEntity.setAverageBid(rate.getAverageBid());
        return currencyRepos.save(rateEntity);
    }

    public void deleteExchangeRate(String baseCurrency) {
        currencyRepos.deleteByBaseCurrency(baseCurrency);
    }

    public void deleteExchangeRateAtTime(String baseCurrency, String update_time) {
        currencyRepos.deleteByBaseCurrencyAndUpdateTime(baseCurrency,
                LocalDateTime.parse(update_time, FMT));
    }

}
