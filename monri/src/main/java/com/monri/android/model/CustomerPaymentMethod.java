package com.monri.android.model;

public class CustomerPaymentMethod {
    private final String status;
    private final String id;
    private final String maskedPan;
    private final String expirationDate;
    private final String keepUntil;
    private final String createdAt;
    private final String updatedAt;
    private final String customerUuid;
    private final String token;
    private final boolean expired;

    public CustomerPaymentMethod(
            final String status,
            final String id,
            final String maskedPan,
            final String expirationDate,
            final String keepUntil,
            final String createdAt,
            final String updatedAt,
            final String customerUuid,
            final String token,
            final boolean expired
    ) {
        this.status = status;
        this.id = id;
        this.maskedPan = maskedPan;
        this.expirationDate = expirationDate;
        this.keepUntil = keepUntil;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customerUuid = customerUuid;
        this.token = token;
        this.expired = expired;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getKeepUntil() {
        return keepUntil;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public String getToken() {
        return token;
    }

    public boolean isExpired() {
        return expired;
    }
}
