package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class PaymentStatusParams implements Parcelable {
    @JsonProperty("client_secret")
    String clientSecret;

    public PaymentStatusParams(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public PaymentStatusParams() {
    }

    public String getClientSecret() {
        return clientSecret;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clientSecret);
    }

    protected PaymentStatusParams(Parcel in) {
        this.clientSecret = in.readString();
    }

    public static final Parcelable.Creator<PaymentStatusParams> CREATOR = new Parcelable.Creator<PaymentStatusParams>() {
        @Override
        public PaymentStatusParams createFromParcel(Parcel source) {
            return new PaymentStatusParams(source);
        }

        @Override
        public PaymentStatusParams[] newArray(int size) {
            return new PaymentStatusParams[size];
        }
    };
}
