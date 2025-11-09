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

    @Autowired
    CurrencyRepos currencyRepos;

    @Autowired
    MessageSource messageSource;

    public void addExchangeRate(String baseCurrency, String quoteCurrency, String update_time, RateDto rate) {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime formatedDatetime = LocalDateTime.parse(update_time, FMT);
        List<CurrencyExchangeRate> existingRates = getExchangeRateAtTime(
                baseCurrency, quoteCurrency, formatedDatetime.toString());
        if (existingRates != null && !existingRates.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("insertMutlipleError", null, null));
        }

        CurrencyExchangeRate rateEntity = new CurrencyExchangeRate();
        rateEntity.setBaseCurrency(baseCurrency);
        rateEntity.setQuoteCurrency(quoteCurrency);
        rateEntity.setUpdateTime(formatedDatetime);
        rateEntity.setHighBid(rate.getHighBid());
        rateEntity.setLowBid(rate.getLowBid());
        rateEntity.setHighAsk(rate.getHighAsk());
        rateEntity.setLowAsk(rate.getLowAsk());
        rateEntity.setAverageAsk(rate.getAverageAsk());
        rateEntity.setAverageBid(rate.getAverageBid());
        currencyRepos.save(rateEntity);
    }

    public List<CurrencyExchangeRate> getExchangeRate(String baseCurrency, String quoteCurrency) {
        List<CurrencyExchangeRate> result = currencyRepos.findByBaseCurrencyAndQuoteCurrencyOrderByBaseCurrency(
                baseCurrency, quoteCurrency);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCodeNotFound", null, null));
        }
        return result;
    }

    public List<CurrencyExchangeRate> getExchangeRateAtTime(String baseCurrency, String quoteCurrency, String time) {
        LocalDateTime parsed = LocalDateTime.parse(time);
        List<CurrencyExchangeRate> result
                = currencyRepos.findByBaseCurrencyAndQuoteCurrencyAndUpdateTimeOrderByBaseCurrency(
                baseCurrency, quoteCurrency, parsed);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCopeWithTimeNotFound", null, null));
        }
        return result;
    }

    public List<CurrencyExchangeRate> getExchangeRateByBaseCurrencyCode(String baseCurrency, String time) {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime updateTime = LocalDateTime.parse(time, FMT);
        List<CurrencyExchangeRate> result = currencyRepos.findByBaseCurrency(baseCurrency, updateTime);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCodeNotFound", null, null));
        }
        return result;
    }

    public List<CurrencyExchangeRate> getExchangeRateByQuoteCurrencyCode(String quoteCurrency, String time) {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime updateTime = LocalDateTime.parse(time, FMT);
        List<CurrencyExchangeRate> result = currencyRepos.findByQuoteCurrency(quoteCurrency, updateTime);
        if (result == null || result.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("currencyCodeNotFound", null, null));
        }
        return result;
    }

    public CurrencyExchangeRate updateExchangeRate(String baseCurrency, String quoteCurrency, String update_time, RateDto rate) {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime formatedDatetime = LocalDateTime.parse(update_time, FMT);
        List<CurrencyExchangeRate> existingRates = getExchangeRateAtTime(
                baseCurrency, quoteCurrency, formatedDatetime.toString());
        if (existingRates != null && !existingRates.isEmpty()) {
            throw new UnsupportedOperationException(
                    messageSource.getMessage("updateCurrencyExchangeNotFound", null, null));
        }
        CurrencyExchangeRate rateEntity = currencyRepos.findByBaseCurrencyAndQuoteCurrencyAndUpdateTime(baseCurrency,
                quoteCurrency, LocalDateTime.parse(update_time));
        rateEntity.setHighBid(rate.getHighBid());
        rateEntity.setLowBid(rate.getLowBid());
        rateEntity.setHighAsk(rate.getHighAsk());
        rateEntity.setLowAsk(rate.getLowAsk());
        rateEntity.setAverageAsk(rate.getAverageAsk());
        rateEntity.setAverageBid(rate.getAverageBid());
        return currencyRepos.save(rateEntity);
    }

    public void deleteExchangeRate(String baseCurrency, String quoteCurrency) {
        currencyRepos.deleteByBaseCurrencyAndQuoteCurrency(baseCurrency, quoteCurrency);
    }

    public void deleteExchangeRateAtTime(String baseCurrency, String quoteCurrency, String update_time) {
        currencyRepos.deleteByBaseCurrencyAndQuoteCurrencyAndUpdateTime(baseCurrency, quoteCurrency,
                LocalDateTime.parse(update_time));
    }

    public void deleteExchangeRateByBaseCurrencyCode(String baseCurrency, String update_time) {
        currencyRepos.deleteByBaseCurrency(baseCurrency, LocalDateTime.parse(update_time));
    }

    public void deleteExchangeRateByQuoteCurrencyCode(String quoteCurrency, String update_time) {
        currencyRepos.deleteByQuoteCurrency(quoteCurrency, LocalDateTime.parse(update_time));
    }

}
