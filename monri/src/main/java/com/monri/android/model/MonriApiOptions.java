package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class MonriApiOptions implements Parcelable {

    private static final String TEST_ENV_HOST = "https://ipgtest.monri.com";
    private static final String PROD_ENV_HOST = "https://ipg.monri.com";

    @JsonProperty("authenticity_token")
    String authenticityToken;

    @JsonProperty("development_mode")
    boolean developmentMode;

    public MonriApiOptions() {
    }

    public MonriApiOptions(String authenticityToken, boolean developmentMode) {
        this.authenticityToken = authenticityToken;
        this.developmentMode = developmentMode;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    public static MonriApiOptions create(String authenticityToken, boolean developmentMode) {
        return new MonriApiOptions(authenticityToken, developmentMode);
    }

    public String url() {
        return isDevelopmentMode() ? TEST_ENV_HOST : PROD_ENV_HOST;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.authenticityToken);
        dest.writeByte(this.developmentMode ? (byte) 1 : (byte) 0);
    }

    protected MonriApiOptions(Parcel in) {
        this.authenticityToken = in.readString();
        this.developmentMode = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MonriApiOptions> CREATOR = new Parcelable.Creator<MonriApiOptions>() {
        @Override
        public MonriApiOptions createFromParcel(Parcel source) {
            return new MonriApiOptions(source);
        }

        @Override
        public MonriApiOptions[] newArray(int size) {
            return new MonriApiOptions[size];
        }
    };
}
