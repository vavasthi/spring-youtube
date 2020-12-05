package in.springframework.learning.tutorial.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TokenUtilities {

    public static String generateToken() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }
    public static ExpiryValues generateTokenExiry() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        Date expiry = calendar.getTime();
        calendar.add(Calendar.HOUR, 24);
        Date refreshExpiry = calendar.getTime();
        return new ExpiryValues(expiry, refreshExpiry);
    }
}
