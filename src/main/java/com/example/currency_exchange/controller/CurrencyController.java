package com.example.currency_exchange.controller;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.EncryptResponse;
import com.example.currency_exchange.dto.PublicKeyResponse;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.service.CurrencyClientService;
import com.example.currency_exchange.service.CurrencyService;
import com.example.currency_exchange.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyClientService clientService;

    private volatile String lastPrivateKeyBase64;

    @PostMapping("/add-exchange-rate")
    public void addExchangeRate(@RequestParam String baseCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME) String update_time, @RequestBody RateDto rate) {
        currencyService.addExchangeRate(baseCurrency, update_time, rate);
    }

    @GetMapping("/get-exchange-rate")
    public List<CurrencyExchangeRateDto> getExchangeRateList(@RequestParam String baseCurrency) {
        return currencyService.getExchangeRate(baseCurrency);
    }

    @GetMapping("/get-exchange-rate-at-time")
    public List<CurrencyExchangeRateDto> getExchangeRateListAtTime(@RequestParam String baseCurrency, @RequestParam String time) {
        return currencyService.getExchangeRateAtTime(baseCurrency, time);
    }

    @GetMapping("/get-exchange-rate-by-base-currency-code")
    public List<CurrencyExchangeRateDto> getExchangeRateListByBaseCurrencyCode(@RequestParam String baseCurrency, @RequestParam String time) {
        return currencyService.getExchangeRateByBaseCurrencyCode(baseCurrency, time);

    }

    @PostMapping("/modify-exchange-rate")
    public CurrencyExchangeRate modifyExchangeRate(@RequestParam String baseCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME) String update_time, @RequestBody RateDto rate) {
        return currencyService.updateExchangeRate(baseCurrency, update_time, rate);
    }

    @GetMapping(value = "/get-fxds-exchange-rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyExchangeRateDto> getFxdsexchangeRateList(@RequestParam String baseCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE) LocalDateTime updateTime) {
        LocalDate startDate = updateTime.toLocalDate();
        LocalDate endDate = updateTime.toLocalDate().plusDays(1);

        return clientService.getCurrencyExchangeRates(baseCurrency, startDate, endDate);

    }

    @DeleteMapping("/delete-exchange-rate")
    public void deleteExchangeRate(@RequestParam String baseCurrency) {
        currencyService.deleteExchangeRate(baseCurrency);
    }

    @DeleteMapping("/delete-exchange-rate-at-time")
    public void deleteExchangeRateAtTime(@RequestParam String baseCurrency, @RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME) String update_time) {
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
