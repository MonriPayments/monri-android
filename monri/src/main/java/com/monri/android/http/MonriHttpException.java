package com.monri.android.http;

public class MonriHttpException extends RuntimeException {
    private final MonriHttpExceptionCode code;

    private MonriHttpException(String message, MonriHttpExceptionCode code) {
        super(message);
        this.code = code;
    }

    private MonriHttpException(Throwable cause, MonriHttpExceptionCode code) {
        super(cause);
        this.code = code;
    }

    public MonriHttpExceptionCode getCode() {
        return code;
    }

    static MonriHttpException create(MonriHttpExceptionCode code) {
        return new MonriHttpException(code.toString(), code);
    }

    static MonriHttpException create(String message, MonriHttpExceptionCode code) {
        return new MonriHttpException(message, code);
    }

    public static MonriHttpException create(Throwable cause, MonriHttpExceptionCode code) {
        return new MonriHttpException(cause, code);
    }
}
