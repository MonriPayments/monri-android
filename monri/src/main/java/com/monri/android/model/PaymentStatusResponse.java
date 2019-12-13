package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class PaymentStatusResponse implements Parcelable {

    @JsonProperty("executed")
    boolean executed;

    @JsonProperty("pending")
    boolean pending;

    @Nullable
    @JsonProperty("payment_result")
    PaymentResult paymentResult;


    public PaymentStatusResponse() {
    }

    public PaymentStatusResponse(boolean executed, boolean pending, @Nullable PaymentResult paymentResult) {
        this.executed = executed;
        this.pending = pending;
        this.paymentResult = paymentResult;
    }

    public boolean isExecuted() {
        return executed;
    }

    public boolean isPending() {
        return pending;
    }

    @Nullable
    public PaymentResult getPaymentResult() {
        return paymentResult;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.executed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.pending ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.paymentResult, flags);
    }

    protected PaymentStatusResponse(Parcel in) {
        this.executed = in.readByte() != 0;
        this.pending = in.readByte() != 0;
        this.paymentResult = in.readParcelable(PaymentResult.class.getClassLoader());
    }

    public static final Creator<PaymentStatusResponse> CREATOR = new Creator<PaymentStatusResponse>() {
        @Override
        public PaymentStatusResponse createFromParcel(Parcel source) {
            return new PaymentStatusResponse(source);
        }

        @Override
        public PaymentStatusResponse[] newArray(int size) {
            return new PaymentStatusResponse[size];
        }
    };
}
