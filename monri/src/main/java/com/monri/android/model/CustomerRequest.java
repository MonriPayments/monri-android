package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerRequest {
    private String merchantCustomerId;
    private String description;
    private String email;
    private String name;
    private String phone;
    private Map<String, String> metadata;
    private String zipCode;
    private String city;
    private String address;
    private String country;

    public CustomerRequest(
            final String merchantCustomerId,
            final String description,
            final String email,
            final String name,
            final String phone,
            final Map<String, String> metadata,
            final String zipCode,
            final String city,
            final String address,
            final String country
    ) {
        this.merchantCustomerId = merchantCustomerId;
        this.description = description;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.metadata = metadata;
        this.zipCode = zipCode;
        this.city = city;
        this.address = address;
        this.country = country;
    }

//    public CustomerRequest merchantCustomerId (final String merchantCustomerId){
//        this.merchantCustomerId = merchantCustomerId;
//        return this;
//    }

    public String getMerchantCustomerId() {
        return merchantCustomerId;
    }

    public void setMerchantCustomerId(final String merchantCustomerId) {
        this.merchantCustomerId = merchantCustomerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(final Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject customerJSON = new JSONObject();

        JSONObject metaDataJSON = new JSONObject(this.metadata);
        customerJSON.put("meta_data", metaDataJSON);
        customerJSON.put("merchantCustomerId", merchantCustomerId);
        customerJSON.put("description", description);
        customerJSON.put("email", email);
        customerJSON.put("name", name);
        customerJSON.put("phone", phone);
        customerJSON.put("metadata", metadata);
        customerJSON.put("zipCode", zipCode);
        customerJSON.put("city", city);
        customerJSON.put("address", address);

        return customerJSON;
    }

    public static class CustomerRequestBuilder {
        private String merchantCustomerId;
        private String description;
        private String email;
        private String name;
        private String phone;
        private Map<String, String> metadata;
        private String zipCode;
        private String city;
        private String address;
        private String country;

        public CustomerRequestBuilder merchantCustomerId(String merchantCustomerId) {
            this.merchantCustomerId = merchantCustomerId;
            return this;
        }

        public CustomerRequestBuilder description(String description) {
            this.description = description;
            return this;
        }
        public CustomerRequestBuilder email(String email) {
            this.email = email;
            return this;
        }
        public CustomerRequestBuilder name(String name) {
            this.name = name;
            return this;
        }
        public CustomerRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }
        public CustomerRequestBuilder metadata(final Map<String, String> metadata) {
            this.metadata = new HashMap<>(metadata);
            return this;
        }

        public CustomerRequest build() {
            return new CustomerRequest(
                    this.merchantCustomerId,
                    this.description,
                    this.email,
                    this.name,
                    this.phone,
                    this.metadata,
                    this.zipCode,
                    this.city,
                    this.address,
                    this.country
            );
        }
    }
}
