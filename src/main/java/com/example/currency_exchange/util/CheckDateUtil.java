package com.example.currency_exchange.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Utility for checking and parsing date-time strings in the exact format "yyyy/MM/dd HH:mm:ss".
 * <p>
 * Examples of valid value: "2025/11/01 00:00:00"
 */
public class CheckDateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("uuuu/MM/dd HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);

    private CheckDateUtil() {
        // utility
    }

    /**
     * Returns true if the input string matches the exact pattern yyyy/MM/dd HH:mm:ss and is a valid date-time.
     * Returns false for null, empty, or invalid strings.
     */
    public static boolean isValid(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return false;
        }
        try {
            LocalDateTime.parse(dateTimeStr, FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    /**
     * Parses the input string using the exact pattern yyyy/MM/dd HH:mm:ss.
     * Throws DateTimeParseException if invalid.
     */
    public static LocalDateTime parse(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }
}
