package com.monri.android.model;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class AccessToken {
    private final String value;
    private final Timestamp validUntil;
    private static final Long ACCESS_TOKEN_VALIDITY_PERIOD_MINUTES = 5L;
    private static final Long REFRESH_TOKEN_VALIDITY_PERIOD_MINUTES = 1440L;

    public AccessToken(final String value, final boolean isRefreshToken) {
        this.value = value;

        final Long validityPeriod = (isRefreshToken) ? REFRESH_TOKEN_VALIDITY_PERIOD_MINUTES : ACCESS_TOKEN_VALIDITY_PERIOD_MINUTES;
        this.validUntil = new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(validityPeriod));
    }

    public boolean isValid() {
        return new Timestamp(System.currentTimeMillis()).before(validUntil);
    }

    public String getValue() {
        return value;
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }
}
