package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerRequestBody {
    private String merchantCustomerId;//can not be updated...
    private String description;
    private String email;
    private String name;
    private String phone;
    private Map<String, String> metadata;
    private String zipCode;
    private String city;
    private String address;
    private String country;

    public CustomerRequestBody() {
    }

    public CustomerRequestBody(
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

    public String getMerchantCustomerId() {
        return merchantCustomerId;
    }

    public CustomerRequestBody setMerchantCustomerId(final String merchantCustomerId) {
        this.merchantCustomerId = merchantCustomerId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CustomerRequestBody setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CustomerRequestBody setEmail(final String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomerRequestBody setName(final String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerRequestBody setPhone(final String phone) {
        this.phone = phone;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public CustomerRequestBody setMetadata(final Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public CustomerRequestBody setZipCode(final String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public CustomerRequestBody setCity(final String city) {
        this.city = city;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CustomerRequestBody setAddress(final String address) {
        this.address = address;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public CustomerRequestBody setCountry(final String country) {
        this.country = country;
        return this;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject customerJSON = new JSONObject();

        customerJSON.put("merchant_customer_id", merchantCustomerId);
        customerJSON.put("description", description);
        customerJSON.put("email", email);
        customerJSON.put("name", name);
        customerJSON.put("phone", phone);
        customerJSON.put("zip_code", zipCode);
        customerJSON.put("city", city);
        customerJSON.put("address", address);

        if (metadata != null) {
            JSONObject metaDataJSON = new JSONObject();
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                metaDataJSON.put(entry.getKey(), entry.getValue());
            }
            customerJSON.put("metadata", metaDataJSON);
        }

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

        public CustomerRequestBody build() {
            return new CustomerRequestBody(
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
