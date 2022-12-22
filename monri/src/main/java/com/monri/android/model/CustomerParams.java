package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class CustomerParams implements Parcelable {
    private String customerId;
    private String email;
    private String fullName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String country;

    public CustomerParams() {
    }

    public CustomerParams(String email,
                          String fullName,
                          String address,
                          String city,
                          String zip,
                          String phone,
                          String country) {
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.country = country;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public CustomerParams setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public CustomerParams setEmail(String email) {
        this.email = email;
        return this;
    }

    public CustomerParams setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public CustomerParams setAddress(String address) {
        this.address = address;
        return this;
    }

    public CustomerParams setCity(String city) {
        this.city = city;
        return this;
    }

    public CustomerParams setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public CustomerParams setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public CustomerParams setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeString(this.email);
        dest.writeString(this.fullName);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.zip);
        dest.writeString(this.phone);
        dest.writeString(this.country);
    }

    protected CustomerParams(Parcel in) {
        this.customerId = in.readString();
        this.email = in.readString();
        this.fullName = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.zip = in.readString();
        this.phone = in.readString();
        this.country = in.readString();
    }

    public static final Parcelable.Creator<CustomerParams> CREATOR = new Parcelable.Creator<CustomerParams>() {
        @Override
        public CustomerParams createFromParcel(Parcel source) {
            return new CustomerParams(source);
        }

        @Override
        public CustomerParams[] newArray(int size) {
            return new CustomerParams[size];
        }
    };
}
