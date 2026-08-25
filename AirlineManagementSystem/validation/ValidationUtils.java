package validation;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?\\d{1,4}[-\\s]?)?\\d{10}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$");
    private static final Pattern AIRPORT_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassport(String passport) {
        if (!isNotEmpty(passport)) return false;
        return PASSPORT_PATTERN.matcher(passport.trim().toUpperCase()).matches();
    }

    public static boolean isValidAirportCode(String code) {
        if (!isNotEmpty(code)) return false;
        return AIRPORT_CODE_PATTERN.matcher(code.trim().toUpperCase()).matches();
    }

    public static boolean isValidDate(String dateStr) {
        return DateValidator.isValidDate(dateStr);
    }

    public static boolean isPositiveInt(int val) {
        return val > 0;
    }

    public static boolean isPositiveDouble(double val) {
        return val > 0;
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    public static boolean isValidUsername(String username) {
        return isNotEmpty(username) && username.trim().length() >= 3;
    }

    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.trim().length() >= 4;
    }
}
