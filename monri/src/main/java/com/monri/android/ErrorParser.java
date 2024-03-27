package com.monri.android;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A helper class for parsing errors coming from Monri servers.
 */
class ErrorParser {

    @VisibleForTesting
    static final String MALFORMED_RESPONSE_MESSAGE =
            "An improperly formatted error response was found.";

    private static final String FIELD_CHARGE = "charge";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_DECLINE_CODE = "decline_code";
    private static final String FIELD_ERROR = "error";
    private static final String FIELD_ERRORS = "errors";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_PARAM = "param";
    private static final String FIELD_TYPE = "type";

    @NonNull
    static MonriError parseError(String rawError) {
        MonriError monriError = new MonriError();
        try {
            JSONObject jsonError = new JSONObject(rawError);
            if (jsonError.has(FIELD_ERRORS)) {
                monriError.message = "";
                JSONArray errorArray = jsonError.getJSONArray(FIELD_ERRORS);
                for (int i = 0; i < errorArray.length(); ++i) {
                    monriError.message += errorArray.getString(i);
                    if (i != 0 && i != errorArray.length() - 1) {
                        monriError.message += ",";
                    }
                }
            } else {
                JSONObject errorObject;
                errorObject = jsonError.getJSONObject(FIELD_ERROR);
                monriError.charge = errorObject.optString(FIELD_CHARGE);
                monriError.code = errorObject.optString(FIELD_CODE);
                monriError.decline_code = errorObject.optString(FIELD_DECLINE_CODE);
                monriError.message = errorObject.optString(FIELD_MESSAGE);
                monriError.param = errorObject.optString(FIELD_PARAM);
                monriError.type = errorObject.optString(FIELD_TYPE);
            }

        } catch (JSONException jsonException) {
            monriError.message = MALFORMED_RESPONSE_MESSAGE;
        }
        return monriError;
    }

    /**
     * A model for error objects sent from the server.
     */
    static class MonriError {
        public String type;

        public String message;

        public String code;

        public String param;

        public String decline_code;

        public String charge;
    }
}