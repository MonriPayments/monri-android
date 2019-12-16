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


    @JsonProperty("payment_status")
    PaymentStatus paymentStatus;

    @JsonProperty("status")
    String status;

    @Nullable
    @JsonProperty("payment_result")
    PaymentResult paymentResult;


    public PaymentStatusResponse() {
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
        dest.writeInt(this.paymentStatus == null ? -1 : this.paymentStatus.ordinal());
        dest.writeString(this.status);
        dest.writeParcelable(this.paymentResult, flags);
    }

    protected PaymentStatusResponse(Parcel in) {
        int tmpPaymentStatus = in.readInt();
        this.paymentStatus = tmpPaymentStatus == -1 ? null : PaymentStatus.values()[tmpPaymentStatus];
        this.status = in.readString();
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
