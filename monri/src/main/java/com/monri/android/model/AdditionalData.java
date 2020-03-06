package com.monri.android.model;

import java.util.Map;

public enum AdditionalData {
    //todo consider min and max length that can be included in regex..
    //todo meta data is map no validation?
    FULL_NAME(ValidationType.REGEX, "ch_full_name", Constants.ALPHA_NUMERIC_REGEX, 3, 30),
    ADDRESS(ValidationType.REGEX, "ch_address", Constants.ALPHA_NUMERIC_REGEX, 3, 100),
    CITY(ValidationType.REGEX, "ch_city", Constants.ALPHA_NUMERIC_REGEX, 3, 30),
    ZIP(ValidationType.REGEX, "ch_zip", Constants.ALPHA_NUMERIC_REGEX, 3, 9),
    COUNTRY(ValidationType.REGEX, "ch_country", Constants.ALPHA_NUMERIC_REGEX, 3, 30),
    PHONE(ValidationType.REGEX, "ch_phone", Constants.PHONE_NUMBER_REGEX, 3, 30),
    EMAIL(ValidationType.REGEX, "ch_email", Constants.EMAIL_REGEX, 3, 100),
    META_DATA(ValidationType.META_DATA_VALIDATION, "meta_data", "", 0, 255);

    private final ValidationType validationType;
    private final String fieldName;
    private final String validationData;
    private final Integer minLength;
    private final Integer maxLength;

    AdditionalData(final ValidationType validationType, final String fieldName,
                   final String validationData,
                   final Integer minLength,
                   final Integer maxLength) {
        this.validationType = validationType;
        this.fieldName = fieldName;
        this.validationData = validationData;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public static AdditionalData fromValue(final String additionalDataKey) {
        switch (additionalDataKey) {
            case "ch_full_name":
                return FULL_NAME;
            case "ch_address":
                return ADDRESS;
            case "ch_city":
                return CITY;
            case "ch_zip":
                return ZIP;
            case "ch_country":
                return COUNTRY;
            case "ch_phone":
                return PHONE;
            case "ch_email":
                return EMAIL;
            case "meta_data":
                return META_DATA;
            default:
                throw new IllegalArgumentException(additionalDataKey + " is not supported ");
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isValid(final Object fieldValue) {
        switch (validationType) {
            case REGEX:
                if (!(fieldValue instanceof String)) {
                    return false;
                }
                final String value = (String) fieldValue;
                return value.matches(validationData) && value.length() >= minLength && value.length() <= maxLength;
            case META_DATA_VALIDATION:
                if (!(fieldValue instanceof Map)) {
                    return false;
                }
                final Map stringObjectMap = (Map) fieldValue;
                return stringObjectMap.size() >= minLength && stringObjectMap.size() <= maxLength;
            default:
                throw new IllegalArgumentException("Unknown validation type");
        }

    }

    private static class Constants {
        private static final String ALPHA_NUMERIC_REGEX = "^[\\p{L}\\p{Z}\\p{N}\\.]+$";
        private static final String EMAIL_REGEX ="^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";
        private static final String PHONE_NUMBER_REGEX = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    }
}
