package com.monri.android.activity;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

public class GooglePayHandlerException extends Exception {
    protected enum Error {
        START_PAYMENT_SESSION_ERROR(-1),
        PREPARE_IS_READY_TO_PAY_REQ_ERROR(-2),
        IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR(-3),
        GET_ALLOWED_PAYMENT_METHODS_ERROR(-4),
        PREPARE_PAYMENT_DATA_REQUEST_ERROR(-5),
        PARSE_PAYMENT_METHOD_DATA_JSON_ERROR(-6),
        NOT_READY_TO_PAY_WITH_GOOGLE_PAY_ERROR(-7),
        ERROR_GETTING_PAYMENT_METHOD_FROM_USER(-8);

        private final int code;

        Error(final int code) {
            this.code = code;
        }

        private int getCode() {
            return code;
        }
    }

    private final int errorCode;
    private int subCode;
    private static final String ERROR_MESSAGE_STRING_FORMAT = "Google pay error with code: %d, sub-code: %d";

    protected GooglePayHandlerException(final Error error) {
        this.errorCode = error.getCode();
    }

    protected GooglePayHandlerException(final Error error, final int subCode) {
        this.errorCode = error.getCode();
        this.subCode = subCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format(ERROR_MESSAGE_STRING_FORMAT, getErrorCode(), subCode);
    }
}
