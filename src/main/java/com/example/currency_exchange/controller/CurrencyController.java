package com.example.currency_exchange.controller;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.EncryptResponse;
import com.example.currency_exchange.dto.PublicKeyResponse;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.service.CurrencyClientService;
import com.example.currency_exchange.service.CurrencyService;
import com.example.currency_exchange.util.CheckDateUtil;
import com.example.currency_exchange.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyClientService clientService;

    @Autowired
    MessageSource messageSource;

    private volatile String lastPrivateKeyBase64;

    @PostMapping("/add-exchange-rate")
    public void addExchangeRate(@RequestParam String baseCurrency, @RequestParam String update_time, @RequestBody RateDto rate) {
        if (!CheckDateUtil.isValid(update_time)) {
            throw new IllegalArgumentException(messageSource.getMessage("wrongDateFormat", null, null));
        }
        currencyService.addExchangeRate(baseCurrency, update_time, rate);
    }

    @GetMapping("/get-exchange-rate")
    public List<CurrencyExchangeRateDto> getExchangeRateList(@RequestParam String baseCurrency) {
        return currencyService.getExchangeRate(baseCurrency);
    }

    @GetMapping("/get-exchange-rate-at-time")
    public List<CurrencyExchangeRateDto> getExchangeRateListAtTime(@RequestParam String baseCurrency, @RequestParam String time) {
        if (!CheckDateUtil.isValid(time)) {
            throw new IllegalArgumentException(messageSource.getMessage("wrongDateFormat", null, null));
        }
        return currencyService.getExchangeRateAtTime(baseCurrency, time);
    }

    @GetMapping("/get-exchange-rate-by-base-currency-code")
    public List<CurrencyExchangeRateDto> getExchangeRateListByBaseCurrencyCode(@RequestParam String baseCurrency, @RequestParam String time) {
        if (!CheckDateUtil.isValid(time)) {
            throw new IllegalArgumentException(messageSource.getMessage("wrongDateFormat", null, null));
        }
        return currencyService.getExchangeRateByBaseCurrencyCode(baseCurrency, time);

    }

    @PostMapping("/modify-exchange-rate")
    public CurrencyExchangeRate modifyExchangeRate(@RequestParam String baseCurrency, @RequestParam String update_time, @RequestBody RateDto rate) {
        if (!CheckDateUtil.isValid(update_time)) {
            throw new IllegalArgumentException(messageSource.getMessage("wrongDateFormat", null, null));
        }
        return currencyService.updateExchangeRate(baseCurrency, update_time, rate);
    }

    @GetMapping(value = "/get-fxds-exchange-rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyExchangeRateDto> getFxdsExchangeRateList(@RequestParam String baseCurrency, @RequestParam String updateTime) {
        if (!CheckDateUtil.isValid(updateTime)) {
            throw new IllegalArgumentException(messageSource.getMessage("wrongDateFormat", null, null));
        }
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDate startDate = LocalDate.parse(updateTime, FMT);
        LocalDate endDate = LocalDate.parse(updateTime, FMT).plusDays(1);

        return clientService.getCurrencyExchangeRates(baseCurrency, startDate, endDate);

    }

    @GetMapping(value = "/call-fxds-exchange-rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyExchangeRateDto> callFxdsExchangeRateList(@RequestParam String baseCurrency, @RequestParam String startDate, String endDate) {
        if (!CheckDateUtil.isValid(startDate) || !CheckDateUtil.isValid(endDate)) {
            throw new IllegalArgumentException(messageSource.getMessage("wrongDateFormat", null, null));
        }
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDate start = LocalDate.parse(startDate, FMT);
        LocalDate end = LocalDate.parse(endDate, FMT);
        return clientService.getCurrencyExchangeRates(baseCurrency, start, end);
    }

    @DeleteMapping("/delete-exchange-rate")
    public void deleteExchangeRate(@RequestParam String baseCurrency) {
        currencyService.deleteExchangeRate(baseCurrency);
    }

    @DeleteMapping("/delete-exchange-rate-at-time")
    public void deleteExchangeRateAtTime(@RequestParam String baseCurrency, @RequestParam String update_time) {
        currencyService.deleteExchangeRateAtTime(baseCurrency, update_time);
    }

    @GetMapping("/rsa/encrypt")
    public EncryptResponse getExchangeRateListWithEncryptCurrencyCode(@RequestParam String text, @RequestBody String publicKeyBase64) {
        return new EncryptResponse(RSAUtil.encrypt(text, RSAUtil.publicKeyFromBase64(publicKeyBase64)));
    }

    @GetMapping("/get-exchange-rate-with-encrypt-currency-code")
    public List<CurrencyExchangeRateDto> rsaDecrypt(@RequestBody String cipher) {
        String currencyCode = RSAUtil.decrypt(cipher, RSAUtil.privateKeyFromBase64(lastPrivateKeyBase64));
        return currencyService.getExchangeRate(currencyCode);
    }

    @GetMapping("/rsa/generate")
    public PublicKeyResponse rsaGenerate() {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        lastPrivateKeyBase64 = RSAUtil.privateKeyToBase64(kp.getPrivate());
        return new PublicKeyResponse(RSAUtil.publicKeyToBase64(kp.getPublic()));
    }

}
