package com.monri.android.model;

public class DeleteCustomerParams {
   final String customerUuid;
   final String accessToken;

   public DeleteCustomerParams(final String accessToken, final String customerUuid ) {
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
