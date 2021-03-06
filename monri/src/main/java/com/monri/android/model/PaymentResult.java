package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class PaymentResult implements Parcelable {

    public static final String BUNDLE_NAME = "BNLD_PaymentResult";
    @JsonProperty("status")
    String status;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("amount")
    Integer amount;

    @JsonProperty("order_number")
    String orderNumber;

    @Nullable
    @JsonProperty("pan_token")
    String panToken;

    @JsonProperty("created_at")
    String createdAt;

    @JsonProperty("transaction_type")
    String transactionType;

    @JsonProperty("payment_method")
    SavedPaymentMethod paymentMethod;

    @JsonProperty("errors")
    List<String> errors;

    public PaymentResult() {
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

    public static final Creator<PaymentResult> CREATOR = new Creator<PaymentResult>() {
        @Override
        public PaymentResult createFromParcel(Parcel source) {
            return new PaymentResult(source);
        }

        @Override
        public PaymentResult[] newArray(int size) {
            return new PaymentResult[size];
        }
    };
}
