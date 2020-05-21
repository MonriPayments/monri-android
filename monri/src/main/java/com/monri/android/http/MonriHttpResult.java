package com.monri.android.http;

public class MonriHttpResult<T> {
    private T result;
    private MonriHttpException cause;

    public MonriHttpResult(final T result, final MonriHttpException cause) {
        this.result = result;
        this.cause = cause;
    }

    static <T> MonriHttpResult<T> failed(MonriHttpException e) {
        return new MonriHttpResult<>(null, e);
    }

    static <T> MonriHttpResult<T> success(T data) {
        return new MonriHttpResult<>(data, null);
    }

    public T getResult() {
        return result;
    }

    public MonriHttpException getCause() {
        return cause;
    }
}
