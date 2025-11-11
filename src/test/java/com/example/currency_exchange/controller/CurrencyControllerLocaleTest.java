package com.example.currency_exchange.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyControllerLocaleTest {

    @Mock
    private LocaleResolver localeResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CurrencyController controller;

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

