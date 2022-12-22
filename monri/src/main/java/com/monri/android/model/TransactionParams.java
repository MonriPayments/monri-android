package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class TransactionParams implements Parcelable {

    private Map<String, String> data;


    public TransactionParams() {
    }

    public TransactionParams(Map<String, String> data) {
        this.data = data;
    }

    public static TransactionParams create() {
        return new TransactionParams();
    }

    public Map<String, String> getData() {
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }

    public TransactionParams set(@NonNull String key, @Nullable String value) {
        if (value == null) {
            getData().remove(key);
        } else {
            getData().put(key, value);
        }
        return this;
    }

    public TransactionParams set(CustomerParams customerParams) {
        if (customerParams == null) {
            return this;
        }

        return set("ch_full_name", customerParams.getFullName())
                .set("customer_id", customerParams.getCustomerId())
                .set("ch_address", customerParams.getAddress())
                .set("ch_city", customerParams.getCity())
                .set("ch_zip", customerParams.getZip())
                .set("ch_phone", customerParams.getPhone())
                .set("ch_country", customerParams.getCountry())
                .set("ch_email", customerParams.getEmail());
    }

    @Nullable
    public Object remove(String key) {
        return getData().remove(key);
    }

    @Nullable
    public Object get(String key) {
        return getData().get(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.data.size());
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    protected TransactionParams(Parcel in) {
        int dataSize = in.readInt();
        this.data = new HashMap<String, String>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.data.put(key, value);
        }
    }

    public static final Creator<TransactionParams> CREATOR = new Creator<TransactionParams>() {
        @Override
        public TransactionParams createFromParcel(Parcel source) {
            return new TransactionParams(source);
        }

        @Override
        public TransactionParams[] newArray(int size) {
            return new TransactionParams[size];
        }
    };
}
