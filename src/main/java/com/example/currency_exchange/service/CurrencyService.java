package com.example.currency_exchange.service;

import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.repo.CurrencyRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CurrencyService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    @Autowired
    CurrencyRepos currencyRepos;
    @Autowired
    MessageSource messageSource;

    public void addExchangeRate(String baseCurrency, String update_time, RateDto rate) {
        List<CurrencyExchangeRate> existingRates = getExchangeRateAtTime(
                baseCurrency, update_time);
        if (existingRates != null && !existingRates.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("insertMutlipleError", null, null));
        }

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
        currencyRepos.save(rateEntity);
    }

    public List<CurrencyExchangeRate> getExchangeRate(String baseCurrency) {
        List<CurrencyExchangeRate> result = currencyRepos.findByBaseCurrencyOrderByBaseCurrency(
                baseCurrency);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCodeNotFound", null, null));
        }
        return result;
    }

    public List<CurrencyExchangeRate> getExchangeRateAtTime(String baseCurrency, String time) {
        LocalDateTime parsed = LocalDateTime.parse(time, FMT);
        List<CurrencyExchangeRate> result
                = currencyRepos.findByBaseCurrencyAndUpdateTimeOrderByBaseCurrency(
                baseCurrency, parsed);
        return result;
    }

    public List<CurrencyExchangeRate> getExchangeRateByBaseCurrencyCode(String baseCurrency, String time) {
        LocalDateTime updateTime = LocalDateTime.parse(time, FMT);
        List<CurrencyExchangeRate> result = currencyRepos.findByBaseCurrency(baseCurrency, updateTime);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCodeNotFound", null, null));
        }
        return result;
    }

    public CurrencyExchangeRate updateExchangeRate(String baseCurrency, String update_time, RateDto rate) {
        CurrencyExchangeRate rateEntity = currencyRepos.findByBaseCurrencyAndUpdateTime(baseCurrency,
                LocalDateTime.parse(update_time, FMT));
        if (rateEntity == null) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("updateCurrencyExchangeNotFound", null, null));
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
