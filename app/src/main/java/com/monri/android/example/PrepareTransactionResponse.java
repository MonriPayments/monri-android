package com.monri.android.example;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-11-13.
 * MonriAndroid
 */
class PrepareTransactionResponse implements Parcelable {
    @JsonProperty("token")
    String token;
    @JsonProperty("digest")
    String digest;
    @JsonProperty("timestamp")
    String timestamp;

    public PrepareTransactionResponse(String token, String digest, String timestamp) {
        this.token = token;
        this.digest = digest;
        this.timestamp = timestamp;
    }

    public PrepareTransactionResponse() {
    }

    public String getToken() {
        return token;
    }

    public String getDigest() {
        return digest;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.digest);
        dest.writeString(this.timestamp);
    }

    protected PrepareTransactionResponse(Parcel in) {
        this.token = in.readString();
        this.digest = in.readString();
        this.timestamp = in.readString();
    }

    public static final Parcelable.Creator<PrepareTransactionResponse> CREATOR = new Parcelable.Creator<PrepareTransactionResponse>() {
        @Override
        public PrepareTransactionResponse createFromParcel(Parcel source) {
            return new PrepareTransactionResponse(source);
        }

        @Override
        public PrepareTransactionResponse[] newArray(int size) {
            return new PrepareTransactionResponse[size];
        }
    };
}
