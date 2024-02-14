package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class PaymentResult implements Parcelable {

    public static final String BUNDLE_NAME = "BNLD_PaymentResult";
    private String status;

    private String currency;

    private Integer amount;

    private String orderNumber;

    @Nullable
    private String panToken;

    private String createdAt;

    private String transactionType;

    private SavedPaymentMethod paymentMethod;

    private List<String> errors;

    public PaymentResult() {
    }

    public PaymentResult(final String status,
                         final String currency,
                         final Integer amount,
                         final String orderNumber,
                         @Nullable final String panToken,
                         final String createdAt,
                         final String transactionType,
                         final SavedPaymentMethod paymentMethod,
                         final List<String> errors) {
        this.status = status;
        this.currency = currency;
        this.amount = amount;
        this.orderNumber = orderNumber;
        this.panToken = panToken;
        this.createdAt = createdAt;
        this.transactionType = transactionType;
        this.paymentMethod = paymentMethod;
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public PaymentResult(String status) {
        this.status = status;
    }

    public PaymentResult(String status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public PaymentResult setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOrderNumber() {
        return orderNumber;
    }


    @Nullable
    public String getPanToken() {
        return panToken;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public SavedPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    @Nullable
    public List<String> getErrors() {
        return errors;
    }

    @NonNull
    @Override
    public String toString() {
        return "PaymentResult{" +
                "status='" + status + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", orderNumber='" + orderNumber + '\'' +
                ", panToken='" + panToken + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", errors=" + errors +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.currency);
        dest.writeValue(this.amount);
        dest.writeString(this.orderNumber);
        dest.writeString(this.panToken);
        dest.writeString(this.createdAt);
        dest.writeString(this.transactionType);
        dest.writeParcelable(this.paymentMethod, flags);
        dest.writeStringList(this.errors);
    }

    protected PaymentResult(Parcel in) {
        this.status = in.readString();
        this.currency = in.readString();
        this.amount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.orderNumber = in.readString();
        this.panToken = in.readString();
        this.createdAt = in.readString();
        this.transactionType = in.readString();
        this.paymentMethod = in.readParcelable(SavedPaymentMethod.class.getClassLoader());
        this.errors = in.createStringArrayList();
    }

    public static final Creator<PaymentResult> CREATOR = new Creator<>() {
        @Override
        public PaymentResult createFromParcel(Parcel source) {
            return new PaymentResult(source);
        }

        @Override
        public PaymentResult[] newArray(int size) {
            return new PaymentResult[size];
        }
    };

    @VisibleForTesting
    public static PaymentResult fromJSON(JSONObject paymentResultJSON) throws JSONException {
        final String paymentStatusResult = paymentResultJSON.getString("status");
        final String paymentStatusCurrency = paymentResultJSON.getString("currency");
        final Integer paymentStatusAmount = paymentResultJSON.getInt("amount");
        final String paymentStatusOrderNumber = paymentResultJSON.getString("order_number");

        String paymentStatusPanToken = "null";

        if (paymentResultJSON.has("pan_token")) {
            paymentStatusPanToken = paymentResultJSON.getString("pan_token");
        }

        final String paymentStatusCreatedAt = paymentResultJSON.getString("created_at");
        final String paymentStatusTransactionType = paymentResultJSON.getString("transaction_type");

        SavedCardPaymentMethod savedCardPaymentMethod = null;

        if (paymentResultJSON.has("payment_method") && !paymentResultJSON.isNull("payment_method")) {
            final JSONObject paymentStatusPaymentMethodJSON = paymentResultJSON.getJSONObject("payment_method");
            final String paymentStatusPaymentMethodType = paymentStatusPaymentMethodJSON.getString("type");
            final JSONObject pmData = paymentStatusPaymentMethodJSON.getJSONObject("data");
            final String brand = pmData.getString("brand");
            final String issuer = pmData.getString("issuer");
            final String masked = pmData.getString("masked");
            final String expiration_date = pmData.getString("expiration_date");
            final String token = pmData.getString("token");
            final SavedCardPaymentMethod.Data data = new SavedCardPaymentMethod.Data(brand, issuer, masked, expiration_date, token);

            savedCardPaymentMethod = new SavedCardPaymentMethod(
                    paymentStatusPaymentMethodType,
                    data
            );
        }

        List<String> paymentStatusErrors = null;

        if (paymentResultJSON.has("errors")) {
            paymentStatusErrors = new ArrayList<>();
            if (!paymentResultJSON.isNull("errors")) {
                final JSONArray jsonArray = paymentResultJSON.getJSONArray("errors");
                for (int i = 0; i < jsonArray.length(); i++) {
                    paymentStatusErrors.add(jsonArray.get(i).toString());
                }
            }
        }

        return new PaymentResult(
                paymentStatusResult,
                paymentStatusCurrency,
                paymentStatusAmount,
                paymentStatusOrderNumber,
                paymentStatusPanToken,
                paymentStatusCreatedAt,
                paymentStatusTransactionType,
                savedCardPaymentMethod,
                paymentStatusErrors
        );
    }
}
