package com.monri.android.model;

import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class ScanDocCardData {
    private static final String HOLDERS_NAME_KEY = "HoldersName";
    private static final String CARD_NUMBER_KEY = "CardNumber";
    private static final String ISSUED_DATE_KEY = "IssuedDate";
    private static final String EXPIRY_DATE_KEY = "ExpiryDate";
    private static final String LUHN_CHECK_KEY = "LuhnCheck";
    private static final String EXTRACTED_TEXTS_KEY = "ExtractedTexts";
    private static final String IBAN_KEY = "IBAN";
    private static final String READ_KEY = "Read";
    private static final String VALUE_KEY = "Value";
    @Nullable
    private final String holdersName;
    @Nullable
    private final String cardNumber;
    @Nullable
    private final String issuedDate;
    @Nullable
    private final String expiryDate;
    @Nullable
    private final String IBAN;
    @Nullable
    private final String luhnCheck;
    @Nullable
    private final String extractedTexts;

    private ScanDocCardData(@Nullable final String holdersName,
                            @Nullable final String cardNumber,
                            @Nullable final String issuedDate,
                            @Nullable final String expiryDate,
                            @Nullable final String IBAN,
                            @Nullable final String luhnCheck,
                            @Nullable final String extractedTexts) {
        this.holdersName = holdersName;
        this.cardNumber = cardNumber;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.IBAN = IBAN;
        this.luhnCheck = luhnCheck;
        this.extractedTexts = extractedTexts;
    }

    public static ScanDocCardData fromJSON(final JSONObject response) throws JSONException {
        final JSONObject holdersNameObject = response.getJSONObject(HOLDERS_NAME_KEY);
        final JSONObject cardNumberObject = response.getJSONObject(CARD_NUMBER_KEY);
        final JSONObject issuedDateObject = response.getJSONObject(ISSUED_DATE_KEY);
        final JSONObject expiryDateObject = response.getJSONObject(EXPIRY_DATE_KEY);
        final JSONObject ibanObject = response.getJSONObject(IBAN_KEY);
        final JSONObject luhnCheckObject = response.getJSONObject(LUHN_CHECK_KEY);
        final JSONObject extractedTextsObject = response.getJSONObject(EXTRACTED_TEXTS_KEY);

        return new ScanDocCardData(
                holdersNameObject.getBoolean(READ_KEY) ? holdersNameObject.getString(VALUE_KEY) : null,
                cardNumberObject.getBoolean(READ_KEY) ? cardNumberObject.getString(VALUE_KEY) : null,
                issuedDateObject.getBoolean(READ_KEY) ? issuedDateObject.getString(VALUE_KEY) : null,
                expiryDateObject.getBoolean(READ_KEY) ? expiryDateObject.getString(VALUE_KEY) : null,
                ibanObject.getBoolean(READ_KEY) ? ibanObject.getString(VALUE_KEY) : null,
                luhnCheckObject.getBoolean(READ_KEY) ? luhnCheckObject.getString(VALUE_KEY) : null,
                extractedTextsObject.getBoolean(READ_KEY) ? extractedTextsObject.getString(VALUE_KEY) : null
        );
    }

    @Nullable
    public String getHoldersName() {
        return holdersName;
    }

    @Nullable
    public String getCardNumber() {
        return cardNumber;
    }

    @Nullable
    public String getIssuedDate() {
        return issuedDate;
    }

    @Nullable
    public String getExpiryDate() {
        return expiryDate;
    }

    @Nullable
    public String getIBAN() {
        return IBAN;
    }

    @Nullable
    public String getLuhnCheck() {
        return luhnCheck;
    }

    @Nullable
    public String getExtractedTexts() {
        return extractedTexts;
    }
}
