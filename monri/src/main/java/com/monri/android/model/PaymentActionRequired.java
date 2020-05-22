package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class PaymentActionRequired implements Parcelable {
    @JsonProperty("redirect_to")
    private String redirectTo;

    @JsonProperty("acs_url")
    private String acsUrl;

    public PaymentActionRequired(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public PaymentActionRequired() {
    }

    public PaymentActionRequired(final String redirectTo, final String acsUrl) {
        this.redirectTo = redirectTo;
        this.acsUrl = acsUrl;
    }

    public PaymentActionRequired setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
        return this;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.redirectTo);
        dest.writeString(this.acsUrl);
    }

    protected PaymentActionRequired(Parcel in) {
        this.redirectTo = in.readString();
        this.acsUrl = in.readString();
    }

    public static final Creator<PaymentActionRequired> CREATOR = new Creator<PaymentActionRequired>() {
        @Override
        public PaymentActionRequired createFromParcel(Parcel source) {
            return new PaymentActionRequired(source);
        }

        @Override
        public PaymentActionRequired[] newArray(int size) {
            return new PaymentActionRequired[size];
        }
    };
}
