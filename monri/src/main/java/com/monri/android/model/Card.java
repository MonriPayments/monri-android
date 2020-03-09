package com.monri.android.model;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.StringDef;

import com.monri.android.CardUtils;
import com.monri.android.MonriTextUtils;
import com.monri.android.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.monri.android.MonriTextUtils.nullIfBlank;


/**
 * A model object representing a Card in the Android SDK.
 */
public class Card extends PaymentMethod {

    @Override
    public String paymentMethodType() {
        return PaymentMethod.TYPE_CARD;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Map<String, String> data() {
        Map<String, String> data = new HashMap<>();

        data.put("pan", nullIfBlank(getNumber()));
        data.put("expiration_date", String.format("%d%02d", getExpYear() - 2000, getExpMonth()));
        data.put("cvv", nullIfBlank(getCVC()));
        data.put("tokenize_pan", Boolean.toString(isTokenizePan()));
        return data;
    }


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            AMERICAN_EXPRESS,
            DISCOVER,
            JCB,
            DINERS_CLUB,
            VISA,
            MASTERCARD,
            UNIONPAY,
            UNKNOWN
    })
    public @interface CardBrand {
    }

    public static final String AMERICAN_EXPRESS = "American Express";
    public static final String DISCOVER = "Discover";
    public static final String JCB = "JCB";
    public static final String DINERS_CLUB = "Diners Club";
    public static final String VISA = "Visa";
    public static final String MASTERCARD = "MasterCard";
    public static final String UNIONPAY = "UnionPay";
    public static final String UNKNOWN = "Unknown";

    public static final int CVC_LENGTH_AMERICAN_EXPRESS = 4;
    public static final int CVC_LENGTH_COMMON = 3;

    public static final String FUNDING_CREDIT = "credit";
    public static final String FUNDING_DEBIT = "debit";
    public static final String FUNDING_PREPAID = "prepaid";
    public static final String FUNDING_UNKNOWN = "unknown";

    public static final Map<String, Integer> BRAND_RESOURCE_MAP =
            new HashMap<String, Integer>() {{
                put(Card.AMERICAN_EXPRESS, R.drawable.ic_amex);
                put(Card.DINERS_CLUB, R.drawable.ic_diners);
                put(Card.DISCOVER, R.drawable.ic_discover);
                put(Card.JCB, R.drawable.ic_jcb);
                put(Card.MASTERCARD, R.drawable.ic_mastercard);
                put(Card.VISA, R.drawable.ic_visa);
                put(Card.UNIONPAY, R.drawable.ic_unionpay);
                put(Card.UNKNOWN, R.drawable.ic_unknown);
            }};

    // Based on http://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number_.28IIN.29
    public static final String[] PREFIXES_AMERICAN_EXPRESS = {"34", "37"};
    public static final String[] PREFIXES_DISCOVER = {"60", "64", "65"};
    public static final String[] PREFIXES_JCB = {"35"};
    public static final String[] PREFIXES_DINERS_CLUB = {"300", "301", "302", "303", "304",
            "305", "309", "36", "38", "39"};
    public static final String[] PREFIXES_VISA = {"4"};
    public static final String[] PREFIXES_MASTERCARD = {
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229",
            "223", "224", "225", "226", "227", "228", "229",
            "23", "24", "25", "26",
            "270", "271", "2720",
            "50", "51", "52", "53", "54", "55", "67"
    };
    public static final String[] PREFIXES_UNIONPAY = {"62"};

    public static final int MAX_LENGTH_STANDARD = 16;
    public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
    public static final int MAX_LENGTH_DINERS_CLUB = 14;

    private String number;
    private String cvc;
    private Integer expMonth;
    private Integer expYear;
    private boolean tokenizePan;
    private Map<String, Object> data;

    @Size(4)
    private String last4;
    @CardBrand
    private String brand;

    /**
     * Converts an unchecked String value to a {@link CardBrand} or {@code null}.
     *
     * @param possibleCardType a String that might match a {@link CardBrand} or be empty.
     * @return {@code null} if the input is blank, else the appropriate {@link CardBrand}.
     */
    @Nullable
    @CardBrand
    public static String asCardBrand(@Nullable String possibleCardType) {
        if (possibleCardType == null || TextUtils.isEmpty(possibleCardType.trim())) {
            return null;
        }

        if (Card.AMERICAN_EXPRESS.equalsIgnoreCase(possibleCardType)) {
            return Card.AMERICAN_EXPRESS;
        } else if (Card.MASTERCARD.equalsIgnoreCase(possibleCardType)) {
            return Card.MASTERCARD;
        } else if (Card.DINERS_CLUB.equalsIgnoreCase(possibleCardType)) {
            return Card.DINERS_CLUB;
        } else if (Card.DISCOVER.equalsIgnoreCase(possibleCardType)) {
            return Card.DISCOVER;
        } else if (Card.JCB.equalsIgnoreCase(possibleCardType)) {
            return Card.JCB;
        } else if (Card.VISA.equalsIgnoreCase(possibleCardType)) {
            return Card.VISA;
        } else if (Card.UNIONPAY.equalsIgnoreCase(possibleCardType)) {
            return Card.UNIONPAY;
        } else {
            return Card.UNKNOWN;
        }
    }

    /**
     * @param number   the card number
     * @param expMonth the expiry month
     * @param expYear  the expiry year
     * @param cvc      the CVC code
     * @deprecated public constructor will be removed in next version, use Card.create(number, expMonth, expYear,cvc) instead.
     * <p>
     * Convenience constructor for a Card object with a minimum number of inputs.
     */
    @Deprecated
    public Card(
            String number,
            Integer expMonth,
            Integer expYear,
            String cvc) {

        this(number, expMonth, expYear, cvc, new HashMap<>());
    }

    /**
     * @param number   the card number
     * @param expMonth the expiry month
     * @param expYear  the expiry year
     * @param cvc      the CVC code
     * @param data additional data for name, zip, address, country, email
     */
    private Card(String number,
                 Integer expMonth,
                 Integer expYear,
                 String cvc,
                 Map<String, Object> data) {
        this.number = MonriTextUtils.nullIfBlank(normalizeCardNumber(number));
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = MonriTextUtils.nullIfBlank(cvc);
        this.brand = getBrand();
        this.last4 = MonriTextUtils.nullIfBlank(last4) == null ? getLast4() : last4;
        this.data = data;
    }

    /**
     * Builder class for Card model
     */
    public static class CardBuilder {

        private final Map<String, Object> data;
        private final Card card;

        /**
         * @param number   the credit card number
         * @param expMonth the expiry month, as an integer value between 1 and 12
         * @param expYear  expiry year
         * @param cvc      the card CVC number
         * @param data additional data for name, zip, address, country, email
         */
        private CardBuilder(final String number,
                            final Integer expMonth,
                            final Integer expYear,
                            final String cvc,
                            Map<String, Object> data) {

            this.data = new HashMap<>(data);
            this.card = new Card(number, expMonth, expYear, cvc);
        }

        public CardBuilder name(final String name) {
            this.data.put(AdditionalData.Constants.FULL_NAME, name);
            return this;
        }

        public String getName() {
            return (String) data.get(AdditionalData.Constants.FULL_NAME);
        }

        public CardBuilder address(final String address) {
            this.data.put(AdditionalData.Constants.ADDRESS, address);
            return this;
        }

        public String getAddress() {
            return (String) data.get(AdditionalData.Constants.ADDRESS);
        }

        public String getCity() {
            return (String) data.get(AdditionalData.Constants.CITY);
        }

        public CardBuilder city(final String city) {
            this.data.put(AdditionalData.Constants.CITY, city);
            return this;
        }

        public CardBuilder zip(final String zip) {
            this.data.put(AdditionalData.Constants.ZIP, zip);
            return this;
        }

        public String getZip() {
            return (String) data.get(AdditionalData.Constants.ZIP);
        }

        public CardBuilder country(final String country) {
            this.data.put(AdditionalData.Constants.COUNTRY, country);
            return this;
        }

        public String getCountry() {
            return (String) data.get(AdditionalData.Constants.COUNTRY);
        }

        public String getPhone() {
            return (String) data.get(AdditionalData.Constants.PHONE);
        }

        public CardBuilder phone(final String phone) {
            this.data.put(AdditionalData.Constants.PHONE, phone);
            return this;
        }

        public CardBuilder email(final String email) {
            this.data.put(AdditionalData.Constants.EMAIL, email);
            return this;
        }

        public String getEmail() {
            return (String) data.get(AdditionalData.Constants.EMAIL);
        }

        public CardBuilder data(Map<String, Object> data) {
            this.data.put(AdditionalData.Constants.META_DATA, data);
            return this;
        }


        public Map<String, Object> getData() {
            //noinspection unchecked
            return Collections.unmodifiableMap((Map<String, Object>) data.get(AdditionalData.Constants.META_DATA));
        }

        public boolean validate() {

            return card.validateCard(Calendar.getInstance()) && validateData();

        }

        /**
         * Checks whether or not additional data is valid
         *
         * @return 'true' if all additional data is valid, 'false' otherwise
         */
        private boolean validateData() {

            for (String dataKey : data.keySet()) {
                final Object dataValue = data.get(dataKey);

                if (dataValue == null) {
                    return false;
                }

                final AdditionalData additionalData = AdditionalData.fromValue(dataKey);

                if (!additionalData.isValid(dataValue)) {
                    return false;
                }
            }

            return true;

        }

        /**
         * @return Card instance
         */
        public Card build() {
            card.setData(this.data);
            return card;
        }

    }

    /**
     * @return a CardBuilder populated with the fields of this Card instance
     */
    public CardBuilder toBuilder() {
        return new CardBuilder(number, expMonth, expYear, cvc, data);
    }

    /**
     * @param number   the card number
     * @param expMonth the expiry month
     * @param expYear  the expiry year
     * @param cvc      the CVC code
     * @return Card object with populated fields
     */
    public static Card create(final String number,
                              final Integer expMonth,
                              final Integer expYear,
                              final String cvc) {
        return new CardBuilder(number, expMonth, expYear, cvc, new HashMap<>())
                .build();
    }

    private void setData(final Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    /**
     * Checks whether {@code this} represents a valid card.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    public boolean validateCard() {
        return validateCard(Calendar.getInstance());
    }

    /**
     * Checks whether or not the {@link #number} field is valid.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    public boolean validateNumber() {
        return CardUtils.isValidCardNumber(number);
    }

    /**
     * Checks whether or not the {@link #expMonth} and {@link #expYear} fields represent a valid
     * expiry date.
     *
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean validateExpiryDate() {
        return validateExpiryDate(Calendar.getInstance());
    }

    /**
     * Checks whether or not the {@link #cvc} field is valid.
     *
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean validateCVC() {
        if (MonriTextUtils.isBlank(cvc)) {
            return false;
        }
        String cvcValue = cvc.trim();
        String updatedType = getBrand();
        boolean validLength =
                (updatedType == null && cvcValue.length() >= 3 && cvcValue.length() <= 4)
                        || (AMERICAN_EXPRESS.equals(updatedType) && cvcValue.length() == 4)
                        || cvcValue.length() == 3;

        return ModelUtils.isWholePositiveNumber(cvcValue) && validLength;
    }

    /**
     * Checks whether or not the {@link #expMonth} field is valid.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    public boolean validateExpMonth() {
        return expMonth != null && expMonth >= 1 && expMonth <= 12;
    }

    /**
     * Checks whether or not the {@link #expYear} field is valid.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    boolean validateExpYear(Calendar now) {
        return expYear != null && !ModelUtils.hasYearPassed(expYear, now);
    }

    /**
     * @return the {@link #number} of this card
     */
    public String getNumber() {
        return number;
    }

    /**
     * Setter for the card number. Note that mutating the number of this card object
     * invalidates the {@link #brand} and {@link #last4}.
     *
     * @param number the new {@link #number}
     */
    @Deprecated
    public void setNumber(String number) {
        this.number = number;
        this.brand = null;
        this.last4 = null;
    }

    /**
     * @return the {@link #cvc} for this card
     */
    public String getCVC() {
        return cvc;
    }

    /**
     * @param cvc the new {@link #cvc} code for this card
     */
    @Deprecated
    public void setCVC(String cvc) {
        this.cvc = cvc;
    }

    /**
     * @return the {@link #expMonth} for this card
     */
    @Nullable
    @IntRange(from = 1, to = 12)
    public Integer getExpMonth() {
        return expMonth;
    }

    /**
     * @param expMonth sets the {@link #expMonth} for this card
     */
    @Deprecated
    public void setExpMonth(@Nullable @IntRange(from = 1, to = 12) Integer expMonth) {
        this.expMonth = expMonth;
    }

    /**
     * @return the {@link #expYear} for this card
     */
    public Integer getExpYear() {
        return expYear;
    }

    /**
     * @param expYear sets the {@link #expYear} for this card
     */
    @Deprecated
    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }


    /**
     * @return the {@link #last4} digits of this card. Sets the value based on the {@link #number}
     * if it has not already been set.
     */
    public String getLast4() {
        if (!MonriTextUtils.isBlank(last4)) {
            return last4;
        }

        if (number != null && number.length() > 4) {
            last4 = number.substring(number.length() - 4, number.length());
            return last4;
        }

        return null;
    }

    /**
     * Gets the {@link #brand} of this card, changed from the "type" field. Use {@link #getBrand()}
     * instead.
     *
     * @return the {@link #brand} of this card
     */
    @Deprecated
    @CardBrand
    public String getType() {
        return getBrand();
    }

    /**
     * Gets the {@link #brand} of this card. Updates the value if none has yet been set, or
     * if the {@link #number} has been changed.
     *
     * @return the {@link #brand} of this card
     */
    @CardBrand
    public String getBrand() {
        if (MonriTextUtils.isBlank(brand) && !MonriTextUtils.isBlank(number)) {
            brand = CardUtils.getPossibleCardType(number);
        }

        return brand;
    }

    boolean validateCard(Calendar now) {
        if (cvc == null) {
            return validateNumber() && validateExpiryDate(now);
        } else {
            return validateNumber() && validateExpiryDate(now) && validateCVC();
        }
    }

    boolean validateExpiryDate(Calendar now) {
        if (!validateExpMonth()) {
            return false;
        }
        if (!validateExpYear(now)) {
            return false;
        }
        return !ModelUtils.hasMonthPassed(expYear, expMonth, now);
    }

    public boolean isTokenizePan() {
        return tokenizePan;
    }

    public Card setTokenizePan(boolean tokenizePan) {
        this.tokenizePan = tokenizePan;
        return this;
    }

    private String normalizeCardNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim().replaceAll("\\s+|-", "");
    }
}
