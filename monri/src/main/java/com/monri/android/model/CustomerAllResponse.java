package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerAllResponse {
    private final String status;
    private final List<CustomerResponse> customerResponseList;

    CustomerAllResponse(final String status, final List<CustomerResponse> customerResponseList) {
        this.status = status;
        this.customerResponseList = customerResponseList;
    }

    public String getStatus() {
        return status;
    }

    public List<CustomerResponse> getCustomerResponseList() {
        return customerResponseList;
    }

    public static CustomerAllResponse fromJSON(JSONObject jsonObject) throws JSONException {
        final String status = jsonObject.getString("status");
        List<CustomerResponse> customers = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < jsonArray.length(); i++) {
            customers.add(CustomerResponse.fromJSON(jsonArray.getJSONObject(i)));
        }

        return new CustomerAllResponse(status, customers);
    }
}
