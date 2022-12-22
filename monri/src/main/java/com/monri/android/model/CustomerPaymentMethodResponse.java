package com.monri.android.model;

import java.util.List;

public class CustomerPaymentMethodResponse {
   private final String status;
   private final List<CustomerPaymentMethod> customerPaymentMethodMap;

   public CustomerPaymentMethodResponse(final String status, final List<CustomerPaymentMethod> customerPaymentMethodMap) {
      this.status = status;
      this.customerPaymentMethodMap = customerPaymentMethodMap;
   }

   public String getStatus() {
      return status;
   }

   public List<CustomerPaymentMethod> getCustomerPaymentMethodMap() {
      return customerPaymentMethodMap;
   }
}
