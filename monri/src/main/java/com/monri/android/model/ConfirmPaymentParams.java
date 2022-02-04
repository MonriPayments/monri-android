package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class ConfirmPaymentParams implements Parcelable {

    private String paymentId;

    private PaymentMethodParams paymentMethod;

    private TransactionParams transaction;

    private ConfirmPaymentParams(String paymentId, PaymentMethodParams paymentMethod, TransactionParams transaction) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.transaction = transaction;
    }

    public ConfirmPaymentParams() {
    }

    public static ConfirmPaymentParams create(String paymentId, PaymentMethodParams paymentMethod, TransactionParams transaction) {
        return new ConfirmPaymentParams(paymentId, paymentMethod, transaction);
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentMethodParams getPaymentMethod() {
        return paymentMethod;
    }

    public TransactionParams getTransaction() {
        return transaction;
    }

    public ConfirmPaymentParams setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public ConfirmPaymentParams setPaymentMethod(PaymentMethodParams paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public ConfirmPaymentParams setTransaction(TransactionParams transaction) {
        this.transaction = transaction;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.paymentId);
        dest.writeParcelable(this.paymentMethod, flags);
        dest.writeParcelable(this.transaction, flags);
    }

    protected ConfirmPaymentParams(Parcel in) {
        this.paymentId = in.readString();
        this.paymentMethod = in.readParcelable(PaymentMethodParams.class.getClassLoader());
        this.transaction = in.readParcelable(TransactionParams.class.getClassLoader());
    }

    public static final Creator<ConfirmPaymentParams> CREATOR = new Creator<ConfirmPaymentParams>() {
        @Override
        public ConfirmPaymentParams createFromParcel(Parcel source) {
            return new ConfirmPaymentParams(source);
        }

        @Override
        public ConfirmPaymentParams[] newArray(int size) {
            return new ConfirmPaymentParams[size];
        }
    };
}
