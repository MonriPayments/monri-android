package com.monri.android.model;

public class CustomerDeleteRequest {
   final String customerUuid;
   final String accessToken;

   public CustomerDeleteRequest(final String accessToken, final String customerUuid ) {
      this.customerUuid = customerUuid;
      this.accessToken = accessToken;
   }

   public String getCustomerUuid() {
      return customerUuid;
   }

   public String getAccessToken() {
      return accessToken;
   }
}
