package com.example.currency_exchange.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class CheckDateUtilTest {

    @Test
    void isValid_acceptsCorrectFormat() {
        String s = "2025/11/01 00:00:00";
        assertTrue(CheckDateUtil.isValid(s));
        LocalDateTime dt = CheckDateUtil.parse(s);
        assertEquals(2025, dt.getYear());
        assertEquals(11, dt.getMonthValue());
        assertEquals(1, dt.getDayOfMonth());
        assertEquals(0, dt.getHour());
        assertEquals(0, dt.getMinute());
        assertEquals(0, dt.getSecond());
    }

    @Test
    void isValid_rejectsNullOrBlank() {
        assertFalse(CheckDateUtil.isValid(null));
        assertFalse(CheckDateUtil.isValid(""));
        assertFalse(CheckDateUtil.isValid("   "));
    }

    @Test
    void isValid_rejectsWrongSeparatorOrPattern() {
        assertFalse(CheckDateUtil.isValid("2025-11-01 00:00:00")); // wrong date separator
        assertFalse(CheckDateUtil.isValid("2025/11/01T00:00:00")); // wrong separator between date/time
        assertFalse(CheckDateUtil.isValid("2025/11/01 0:0:0")); // not zero-padded
    }

    @Test
    void isValid_rejectsInvalidCalendarDates() {
        // Feb 30th doesn't exist
        assertFalse(CheckDateUtil.isValid("2025/02/30 12:00:00"));
        // hour 24 is invalid
        assertFalse(CheckDateUtil.isValid("2025/11/01 24:00:00"));
    }

    @Test
    void isValid_acceptsLeapDay() {
        // 2024 is leap year
        assertTrue(CheckDateUtil.isValid("2024/02/29 12:34:56"));
    }

    @Test
    void parse_throwsForInvalid() {
        assertThrows(DateTimeParseException.class, () -> CheckDateUtil.parse("invalid-date"));
        assertThrows(DateTimeParseException.class, () -> CheckDateUtil.parse("2025-11-01 00:00:00"));
    }
}

