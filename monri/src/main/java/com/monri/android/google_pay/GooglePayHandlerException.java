package com.monri.android.google_pay;

public class GooglePayHandlerException extends Exception {
    protected enum Error {
        START_PAYMENT_SESSION_ERROR(-1),
        PREPARE_IS_READY_TO_PAY_REQ_ERROR(-2),
        IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR(-3),
        NOT_READY_TO_PAY_ERROR(-4),
        GET_ALLOWED_PAYMENT_METHODS_ERROR(-5),
        PREPARE_PAYMENT_DATA_REQUEST_ERROR(-6),
        PARSE_PAYMENT_METHOD_DATA_JSON_ERROR(-7);

        private final int code;

        Error(int code) {
            this.code = code;
        }

        protected int getCode() {
            return code;
        }
    };

    private final int errorCode;

    public GooglePayHandlerException(final Error error) {
        this.errorCode = error.getCode();
    }

    public int getErrorCode() {
        return errorCode;
    }
}
