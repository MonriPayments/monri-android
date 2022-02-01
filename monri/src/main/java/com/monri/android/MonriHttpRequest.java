package com.monri.android;

public class MonriHttpRequest<T> {
    private final HttpCallType httpCallType;
    private final T requestData;

    public enum HttpCallType {
        CONFIRM_PAYMENT,
        PAYMENT_STATUS
    }

    public MonriHttpRequest(final HttpCallType httpCallType, final T requestData) {
        this.httpCallType = httpCallType;
        this.requestData = requestData;
    }

    public HttpCallType getHttpCallType() {
        return httpCallType;
    }

    public T getRequestData() {
        return requestData;
    }
}
