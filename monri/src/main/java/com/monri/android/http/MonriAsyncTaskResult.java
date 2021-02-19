package com.monri.android.http;

public class MonriAsyncTaskResult<T> {
    private final T result;
    private final MonriHttpException exception;

    public MonriAsyncTaskResult(final T result, final MonriHttpException exception) {
        this.result = result;
        this.exception = exception;
    }

    public static <T> MonriAsyncTaskResult success(T data) {
        return new MonriAsyncTaskResult<>(data, null);
    }

    public static MonriAsyncTaskResult failed(MonriHttpException throwable) {
        return new MonriAsyncTaskResult<>(null, throwable);
    }

    public static MonriAsyncTaskResult failed(Throwable cause, MonriHttpExceptionCode code) {
        return new MonriAsyncTaskResult<>(null, MonriHttpException.create(cause, code));
    }

    public static MonriAsyncTaskResult failed(MonriHttpExceptionCode code) {
        return new MonriAsyncTaskResult<>(null, MonriHttpException.create(code));
    }

    public T getResult() {
        return result;
    }

    public MonriHttpException getException() {
        return exception;
    }
}
