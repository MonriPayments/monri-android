package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class SavedCardPaymentMethod extends SavedPaymentMethod {
    private String type;

    private Data data;

    public SavedCardPaymentMethod(String type, Data data) {
        this.type = type;
        this.data = data;
    }

    public SavedCardPaymentMethod() {
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SavedCardPaymentMethod{");
        sb.append("type='").append(type).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public Data getData() {
        return data;
    }

    public static class Data implements Parcelable {
        String brand;
        String issuer;
        String masked;
        String expirationDate;
        String token;

        public Data(String brand, String issuer, String masked, String expirationDate, String token) {
            this.brand = brand;
            this.issuer = issuer;
            this.masked = masked;
            this.expirationDate = expirationDate;
            this.token = token;
        }

        public Data() {
        }

        public String getBrand() {
            return brand;
        }

        public String getIssuer() {
            return issuer;
        }

        public String getMasked() {
            return masked;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public String getToken() {
            return token;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.brand);
            dest.writeString(this.issuer);
            dest.writeString(this.masked);
            dest.writeString(this.expirationDate);
            dest.writeString(this.token);
        }

        protected Data(Parcel in) {
            this.brand = in.readString();
            this.issuer = in.readString();
            this.masked = in.readString();
            this.expirationDate = in.readString();
            this.token = in.readString();
        }

        public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel source) {
                return new Data(source);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        @Override
        public String toString() {
            return "Data{" + "brand='" + brand + '\'' +
                    ", issuer='" + issuer + '\'' +
                    ", masked='" + masked + '\'' +
                    ", expirationDate='" + expirationDate + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeParcelable(this.data, flags);
    }

    protected SavedCardPaymentMethod(Parcel in) {
        this.type = in.readString();
        this.data = in.readParcelable(Data.class.getClassLoader());
    }

    public static final Parcelable.Creator<SavedCardPaymentMethod> CREATOR = new Parcelable.Creator<SavedCardPaymentMethod>() {
        @Override
        public SavedCardPaymentMethod createFromParcel(Parcel source) {
            return new SavedCardPaymentMethod(source);
        }

        @Override
        public SavedCardPaymentMethod[] newArray(int size) {
            return new SavedCardPaymentMethod[size];
        }
    };
}
