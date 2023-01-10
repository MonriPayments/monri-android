package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MerchantCustomers {
    private final String status;
    private final List<Customer> customerList;

    MerchantCustomers(final String status, final List<Customer> customerList) {
        this.status = status;
        this.customerList = customerList;
    }

    public String getStatus() {
        return status;
    }

    public List<Customer> getCustomerResponseList() {
        return customerList;
    }

    public static MerchantCustomers fromJSON(JSONObject jsonObject) throws JSONException {
        final String status = jsonObject.getString("status");
        List<Customer> customers = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < jsonArray.length(); i++) {
            customers.add(Customer.fromJSON(jsonArray.getJSONObject(i)));
        }

        return new MerchantCustomers(status, customers);
    }
}
