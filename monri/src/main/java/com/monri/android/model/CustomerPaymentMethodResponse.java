package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerPaymentMethodResponse {
   private final String status;
   private final List<CustomerPaymentMethod> customerPaymentMethodMap;

   public CustomerPaymentMethodResponse(final String status, final List<CustomerPaymentMethod> customerPaymentMethodMap) {
      this.status = status;
      this.customerPaymentMethodMap = customerPaymentMethodMap;
   }

   public static CustomerPaymentMethodResponse fromJSON(final JSONObject result) throws JSONException {
      final String status = result.getString("status");
      final List<CustomerPaymentMethod> allPaymentMethodsFromMerchant = new ArrayList<>();

      final JSONArray jsonArray = result.getJSONArray("data");
      for(int i=0; i<jsonArray.length(); i++){
         final JSONObject jsonObject = jsonArray.getJSONObject(i);
         final CustomerPaymentMethod customerPaymentMethod = CustomerPaymentMethod.fromJSON(jsonObject);
         allPaymentMethodsFromMerchant.add(customerPaymentMethod);
      }

      return new CustomerPaymentMethodResponse(status, allPaymentMethodsFromMerchant);
   }

   public String getStatus() {
      return status;
   }

   public List<CustomerPaymentMethod> getCustomerPaymentMethodMap() {
      return customerPaymentMethodMap;
   }
}
