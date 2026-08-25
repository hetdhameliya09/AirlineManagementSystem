package validation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class DateValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Check if input matches strict yyyy-MM-dd format and is a valid calendar date
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Parse date string safely into LocalDate
    public static LocalDate parseDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
    }

    // Return today's LocalDate
    public static LocalDate today() {
        return LocalDate.now();
    }

    // Return current formatted timestamp (yyyy-MM-dd HH:mm:ss)
    public static String nowTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    // Prompt user for any valid date in yyyy-MM-dd format
    public static LocalDate inputDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (isValidDate(input)) {
                return parseDate(input);
            }
            System.out.println("Invalid date.");
            System.out.println("Please enter date in yyyy-MM-dd format.");
        }
    }

    // Prompt user for date that cannot be in the past (today or future dates only)
    public static LocalDate inputFutureDate(Scanner sc, String prompt) {
        while (true) {
            LocalDate date = inputDate(sc, prompt);
            if (!date.isBefore(today())) {
                return date;
            }
            System.out.println("Date cannot be in the past.");
        }
    }

    // Prompt user for hotel check-out date that MUST be strictly after check-in date
    public static LocalDate inputCheckOutDate(Scanner sc, String prompt, LocalDate checkIn) {
        while (true) {
            LocalDate date = inputDate(sc, prompt);
            if (date.isAfter(checkIn)) {
                return date;
            }
            System.out.println("Check-out date must be after check-in date (" + checkIn + ").");
        }
    }
}
