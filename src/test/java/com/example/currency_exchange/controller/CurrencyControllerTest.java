package com.example.currency_exchange.controller;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.EncryptResponse;
import com.example.currency_exchange.dto.PublicKeyResponse;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.service.CurrencyClientService;
import com.example.currency_exchange.service.CurrencyService;
import com.example.currency_exchange.util.RSAUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private CurrencyClientService clientService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CurrencyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addExchangeRate_delegatesToService() {
        RateDto rate = new RateDto();
        rate.setAverageBid(new BigDecimal("1.23"));

        controller.addExchangeRate("EUR", "2025/11/01 00:00:00", rate);

        verify(currencyService, times(1)).addExchangeRate(eq("EUR"), eq("2025/11/01 00:00:00"), eq(rate));
    }

    @Test
    void getExchangeRateList_returnsServiceResult() {
        CurrencyExchangeRateDto e = new CurrencyExchangeRateDto();
        e.setBaseCurrency("EUR");
        e.setQuoteCurrency("USD");
        List<CurrencyExchangeRateDto> expected = Collections.singletonList(e);

        when(currencyService.getExchangeRate("EUR")).thenReturn(expected);

        List<CurrencyExchangeRateDto> actual = controller.getExchangeRateList("EUR");

        assertSame(expected, actual);
        verify(currencyService, times(1)).getExchangeRate("EUR");
    }

    @Test
    void getGetExchangeRateListAtTime_returnsServiceResult() {
        CurrencyExchangeRateDto e = new CurrencyExchangeRateDto();
        e.setBaseCurrency("EUR");
        e.setQuoteCurrency("USD");
        List<CurrencyExchangeRateDto> expected = Collections.singletonList(e);

        when(currencyService.getExchangeRateAtTime("EUR", "2025/11/01 00:00:00")).thenReturn(expected);

        List<CurrencyExchangeRateDto> actual = controller.getExchangeRateListAtTime("EUR", "2025/11/01 00:00:00");

        assertEquals(1, actual.size());
        // expected baseCurrency is EUR and quoteCurrency is USD as set above
        assertEquals("EUR", actual.getFirst().getBaseCurrency());
        assertEquals("USD", actual.getFirst().getQuoteCurrency());
        verify(currencyService, times(1)).getExchangeRateAtTime("EUR", "2025/11/01 00:00:00");
    }

    @Test
    void getGetExchangeRateListByBaseCurrencyCode_returnsServiceResult() {
        CurrencyExchangeRateDto e = new CurrencyExchangeRateDto();
        e.setBaseCurrency("USD");
        List<CurrencyExchangeRateDto> expected = Collections.singletonList(e);

        when(currencyService.getExchangeRateByBaseCurrencyCode("USD", "2025/11/01 00:00:00")).thenReturn(expected);

        List<CurrencyExchangeRateDto> actual = controller.getExchangeRateListByBaseCurrencyCode("USD", "2025/11/01 00:00:00");

        assertFalse(actual.isEmpty());
        assertEquals("USD", actual.getFirst().getBaseCurrency());
        verify(currencyService, times(1)).getExchangeRateByBaseCurrencyCode("USD", "2025/11/01 00:00:00");
    }

    @Test
    void modifyExchangeRate_returnsUpdatedEntity() {
        RateDto rate = new RateDto();
        rate.setAverageBid(new BigDecimal("2.5"));
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
    void getFxdsExchangeRateList_delegatesToClientService() {
        CurrencyExchangeRateDto dto = new CurrencyExchangeRateDto();
        dto.setBaseCurrency("USD");
        List<CurrencyExchangeRateDto> expected = Collections.singletonList(dto);

        LocalDateTime updateTime = LocalDate.of(2025, 11, 1).atStartOfDay();
        LocalDate startDate = updateTime.toLocalDate();
        LocalDate endDate = updateTime.toLocalDate().plusDays(1);

        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String updateTimeStr = updateTime.format(FMT);

        when(clientService.getCurrencyExchangeRates("USD", startDate, endDate)).thenReturn(expected);

        List<CurrencyExchangeRateDto> actual = controller.getFxdsExchangeRateList("USD", updateTimeStr);

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

    @Test
    void rsaGenerate_returnsPublicKey() {
        PublicKeyResponse resp = controller.rsaGenerate();

        assertNotNull(resp);
        assertNotNull(resp.getPublicKey());
        assertFalse(resp.getPublicKey().isEmpty());
    }

    @Test
    void getExchangeRateListWithEncryptCurrencyCode_encryptsAndCanBeDecrypted() {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        String publicKeyBase64 = RSAUtil.publicKeyToBase64(kp.getPublic());

        EncryptResponse resp = controller.getExchangeRateListWithEncryptCurrencyCode("EUR", publicKeyBase64);

        assertNotNull(resp);
        assertNotNull(resp.getEncryptedData());
        String decrypted = RSAUtil.decrypt(resp.getEncryptedData(), kp.getPrivate());
        assertEquals("EUR", decrypted);
    }

    @Test
    void rsaDecrypt_callsServiceWithDecryptedCurrencyCode() throws Exception {
        // prepare key pair and set controller's private key
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        String publicKeyBase64 = RSAUtil.publicKeyToBase64(kp.getPublic());
        String privateBase64 = RSAUtil.privateKeyToBase64(kp.getPrivate());

        // set private field lastPrivateKeyBase64 via reflection
        Field f = CurrencyController.class.getDeclaredField("lastPrivateKeyBase64");
        f.setAccessible(true);
        f.set(controller, privateBase64);

        // encrypt a currency code with the public key
        String cipher = RSAUtil.encrypt("USD", RSAUtil.publicKeyFromBase64(publicKeyBase64));

        CurrencyExchangeRateDto dto = new CurrencyExchangeRateDto();
        dto.setBaseCurrency("USD");
        List<CurrencyExchangeRateDto> expected = Collections.singletonList(dto);

        when(currencyService.getExchangeRate("USD")).thenReturn(expected);

        List<CurrencyExchangeRateDto> actual = controller.rsaDecrypt(cipher);

        assertSame(expected, actual);
        verify(currencyService, times(1)).getExchangeRate("USD");
    }

    @Test
    void addExchangeRate_invalidDate_throwsIllegalArgumentException() {
        RateDto rate = new RateDto();
        rate.setAverageBid(new BigDecimal("1.23"));

        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad format");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.addExchangeRate("EUR", "2025-11-01T00:00:00Z", rate));
        assertEquals("bad format", ex.getMessage());
    }

    @Test
    void getFxdsExchangeRateList_invalidDate_throwsIllegalArgumentException() {
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad format");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.getFxdsExchangeRateList("USD", "2025-11-01T00:00:00Z"));
        assertEquals("bad format", ex.getMessage());
    }

    @Test
    void callFxdsExchangeRateList_invalidDate_throwsIllegalArgumentException() {
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad format");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.callFxdsExchangeRateList("USD", "2025-11-01T00:00:00Z", "2025-11-02T00:00:00Z"));
        assertEquals("bad format", ex.getMessage());
    }

}
