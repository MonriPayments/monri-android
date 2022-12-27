package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class CustomerResponse {
    private String status;
    private String uuid;
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
    private String deleted;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public CustomerResponse(
            final String status,
            final String uuid,
            final String merchantCustomerId,
            final String description,
            final String email,
            final String name,
            final String phone,
            final Map<String, String> metadata,
            final String zip_code,
            final String city,
            final String address,
            final String country,
            final String deleted,
            final String created_at,
            final String updated_at,
            final String deleted_at
    ) {
        this.status = status;
        this.uuid = uuid;
        this.merchantCustomerId = merchantCustomerId;
        this.description = description;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.metadata = metadata;
        this.zipCode = zip_code;
        this.city = city;
        this.address = address;
        this.country = country;
        this.deleted = deleted;
        this.createdAt = created_at;
        this.updatedAt = updated_at;
        this.deletedAt = deleted_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

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

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(final String deleted) {
        this.deleted = deleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(final String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public static CustomerResponse fromJSON(JSONObject jsonObject) throws JSONException {
        Map<String, String> metadata = new HashMap<>();
        if(jsonObject.has("metadata")){
            Object metaJSONObject = jsonObject.get("metadata");
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = jsonObject.getString(key);
                    metadata.put(key, value);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        }

        return new CustomerResponse(
                jsonObject.getString("status"),
                jsonObject.getString("uuid"),
                jsonObject.getString("merchant_customer_id"),
                jsonObject.getString("description"),
                jsonObject.getString("email"),
                jsonObject.getString("name"),
                jsonObject.getString("phone"),
                metadata,
                jsonObject.getString("zip_code"),
                jsonObject.getString("city"),
                jsonObject.getString("address"),
                jsonObject.getString("country"),
                jsonObject.getString("deleted"),
                jsonObject.getString("created_at"),
                jsonObject.getString("updated_at"),
                jsonObject.getString("deleted_at")
        );
    }
}
