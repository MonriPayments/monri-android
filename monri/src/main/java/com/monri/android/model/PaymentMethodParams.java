package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class PaymentMethodParams implements Parcelable {
    @JsonProperty("type")
    String type;

    @JsonProperty("data")
    Map<String, String> data;

    PaymentMethodParams(String type, Map<String, String> data) {
        this.type = type;
        this.data = data;
    }

    public PaymentMethodParams() {
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public PaymentMethodParams setType(String type) {
        this.type = type;
        return this;
    }

    public PaymentMethodParams setData(Map<String, String> data) {
        this.data = data;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeInt(this.data.size());
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    protected PaymentMethodParams(Parcel in) {
        this.type = in.readString();
        int dataSize = in.readInt();
        this.data = new HashMap<String, String>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.data.put(key, value);
        }
    }

    public static final Parcelable.Creator<PaymentMethodParams> CREATOR = new Parcelable.Creator<PaymentMethodParams>() {
        @Override
        public PaymentMethodParams createFromParcel(Parcel source) {
            return new PaymentMethodParams(source);
        }

        @Override
        public PaymentMethodParams[] newArray(int size) {
            return new PaymentMethodParams[size];
        }
    };
}
