package com.example.currency_exchange.controller;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.dto.EncryptResponse;
import com.example.currency_exchange.dto.PublicKeyResponse;
import com.example.currency_exchange.dto.RateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import com.example.currency_exchange.service.CurrencyClientService;
import com.example.currency_exchange.service.CurrencyService;
import com.example.currency_exchange.util.RSAUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private CurrencyClientService clientService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CurrencyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addExchangeRateSuccess() {
        RateDto rate = new RateDto();
        rate.setAverageBid(new BigDecimal("1.23"));

        controller.addExchangeRate("EUR", "2025/11/01 00:00:00", rate);

        verify(currencyService, times(1)).addExchangeRate(eq("EUR"), eq("2025/11/01 00:00:00"), eq(rate));
    }

    @Test
    void getExchangeRateListSuccess_returnsServiceResult() {
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
    void getGetExchangeRateAtTimeSuccess_returnsServiceResult() {
        CurrencyExchangeRateDto expected = new CurrencyExchangeRateDto();
        expected.setBaseCurrency("EUR");
        expected.setQuoteCurrency("USD");

        when(currencyService.getExchangeRateAtTime("EUR", "2025/11/01 00:00:00")).thenReturn(expected);

        CurrencyExchangeRateDto actual = controller.getExchangeRateAtTime("EUR", "2025/11/01 00:00:00");

        // expected baseCurrency is EUR and quoteCurrency is USD as set above
        assertEquals("EUR", actual.getBaseCurrency());
        assertEquals("USD", actual.getQuoteCurrency());
        verify(currencyService, times(1)).getExchangeRateAtTime("EUR", "2025/11/01 00:00:00");
    }

    @Test
    void modifyExchangeRateSuccess_returnsUpdatedEntity() {
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
    void getFxdsExchangeRateListSuccess() {
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
    void deleteExchangeRateSuccess() {
        doNothing().when(currencyService).deleteExchangeRate("EUR");

        controller.deleteExchangeRate("EUR");

        verify(currencyService, times(1)).deleteExchangeRate("EUR");
    }

    @Test
    void deleteExchangeRateAtTimeSuccess() {
        doNothing().when(currencyService).deleteExchangeRateAtTime("EUR", "2025/11/01 00:00:00");

        controller.deleteExchangeRateAtTime("EUR", "2025/11/01 00:00:00");

        verify(currencyService, times(1)).deleteExchangeRateAtTime("EUR", "2025/11/01 00:00:00");
    }

    @Test
    void rsaGenerateSuccess_returnsPublicKey() {
        PublicKeyResponse resp = controller.rsaGenerate();

        assertNotNull(resp);
        assertNotNull(resp.getPublicKey());
        assertFalse(resp.getPublicKey().isEmpty());
    }

    @Test
    void getExchangeRateListWithEncryptCurrencyCodeSuccess() {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        String publicKeyBase64 = RSAUtil.publicKeyToBase64(kp.getPublic());

        EncryptResponse resp = controller.getExchangeRateListWithEncryptCurrencyCode("EUR", publicKeyBase64);

        assertNotNull(resp);
        assertNotNull(resp.getEncryptedData());
        String decrypted = RSAUtil.decrypt(resp.getEncryptedData(), kp.getPrivate());
        assertEquals("EUR", decrypted);
    }

    @Test
    void rsaDecryptSuccess() throws Exception {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        String publicKeyBase64 = RSAUtil.publicKeyToBase64(kp.getPublic());
        String privateBase64 = RSAUtil.privateKeyToBase64(kp.getPrivate());

        Field f = CurrencyController.class.getDeclaredField("lastPrivateKeyBase64");
        f.setAccessible(true);
        f.set(controller, privateBase64);

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
    void addExchangeRate_invalidTime_throws() {
        RateDto rate = RateDto.builder().averageAsk(BigDecimal.ONE).averageBid(BigDecimal.ONE).build();
        String badTime = "2025-11-06T23:59:59Z"; // controller expects yyyy/MM/dd HH:mm:ss
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("wrong format");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.addExchangeRate("EUR", badTime, rate));
        assertEquals("wrong format", ex.getMessage());
        verify(currencyService, never()).addExchangeRate(anyString(), anyString(), any());
    }

    @Test
    void getExchangeRateAtTime_invalidTime_throws() {
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad time");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.getExchangeRateAtTime("EUR", "2025-11-06T23:59:59Z"));
        assertEquals("bad time", ex.getMessage());
        verify(currencyService, never()).getExchangeRateAtTime(anyString(), anyString());
    }

    @Test
    void getFxdsExchangeRateList_invalidTime_throws() {
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.getFxdsExchangeRateList("EUR", "2025-11-06T23:59:59Z"));
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void callFxdsExchangeRateList_invalidTime_throws() {
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.callFxdsExchangeRateList("EUR", "2025-11-06T23:59:59Z", "2025-11-07T23:59:59Z"));
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void getFxdsExchangeRateListSuccessWithValidTime() {
        String valid = "2025/11/06 00:00:00";
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDate start = LocalDate.parse(valid, FMT);
        LocalDate end = start.plusDays(1);

        CurrencyExchangeRateDto dto = CurrencyExchangeRateDto.builder().baseCurrency("EUR").quoteCurrency("USD").build();
        when(clientService.getCurrencyExchangeRates(eq("EUR"), eq(start), eq(end))).thenReturn(List.of(dto));

        List<CurrencyExchangeRateDto> result = controller.getFxdsExchangeRateList("EUR", valid);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EUR", result.get(0).getBaseCurrency());
        verify(clientService).getCurrencyExchangeRates(eq("EUR"), eq(start), eq(end));
    }

    @Test
    void callFxdsExchangeRateListSuccessWithValidTime() {
        String start = "2025/11/06 00:00:00";
        String end = "2025/11/07 00:00:00";
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDate s = LocalDate.parse(start, FMT);
        LocalDate e = LocalDate.parse(end, FMT);

        CurrencyExchangeRateDto dto = CurrencyExchangeRateDto.builder().baseCurrency("EUR").quoteCurrency("USD").build();
        when(clientService.getCurrencyExchangeRates(eq("EUR"), eq(s), eq(e))).thenReturn(List.of(dto));

        List<CurrencyExchangeRateDto> result = controller.callFxdsExchangeRateList("EUR", start, end);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clientService).getCurrencyExchangeRates(eq("EUR"), eq(s), eq(e));
    }

    @Test
    void modifyExchangeRate_invalidTime_throws() {
        RateDto rate = RateDto.builder().averageAsk(BigDecimal.ONE).averageBid(BigDecimal.ONE).build();
        when(messageSource.getMessage(eq("wrongDateFormat"), any(), any())).thenReturn("bad");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.modifyExchangeRate("EUR", "2025-11-06T23:59:59Z", rate));
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void changeLocale_callsLocaleResolver_withLanguageTag_vi() {
        String lang = "vi";

        controller.changeLocale(lang, request, response);

        ArgumentCaptor<Locale> captor = ArgumentCaptor.forClass(Locale.class);
        verify(localeResolver, times(1)).setLocale(eq(request), eq(response), captor.capture());
        Locale passed = captor.getValue();
        assertEquals(Locale.forLanguageTag("vi"), passed);
    }

    @Test
    void changeLocale_callsLocaleResolver_withLanguageTag_enUS() {
        String lang = "en-US";

        controller.changeLocale(lang, request, response);

        ArgumentCaptor<Locale> captor = ArgumentCaptor.forClass(Locale.class);
        verify(localeResolver, times(1)).setLocale(eq(request), eq(response), captor.capture());
        Locale passed = captor.getValue();
        assertEquals(Locale.forLanguageTag("en-US"), passed);
    }

}
