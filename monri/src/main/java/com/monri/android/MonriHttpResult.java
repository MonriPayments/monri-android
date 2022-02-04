package com.monri.android;

public class MonriHttpResult<T> {
    private T result;
    private MonriHttpException cause;
    private Integer responseCode;

    public MonriHttpResult(final T result, final MonriHttpException cause, Integer responseCode) {
        this.result = result;
        this.cause = cause;
        this.responseCode = responseCode;
    }

    public static <T> MonriHttpResult<T> failed(MonriHttpException e) {
        return new MonriHttpResult<>(null, e, null);
    }

    public static <T> MonriHttpResult<T> success(T data, int responseCode) {
        return new MonriHttpResult<>(data, null, responseCode);
    }

    public T getResult() {
        return result;
    }

    public MonriHttpException getCause() {
        return cause;
    }
}
