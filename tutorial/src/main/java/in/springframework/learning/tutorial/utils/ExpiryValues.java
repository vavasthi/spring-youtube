package in.springframework.learning.tutorial.utils;

import java.util.Date;

public class ExpiryValues {
    public ExpiryValues(Date expiry, Date refreshExpiry) {
        this.expiry = expiry;
        this.refreshExpiry = refreshExpiry;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public Date getRefreshExpiry() {
        return refreshExpiry;
    }

    public void setRefreshExpiry(Date refreshExpiry) {
        this.refreshExpiry = refreshExpiry;
    }

    Date expiry;
    Date refreshExpiry;
}
