package com.monri.android

internal class MonriUrlProvider(baseUrl: String): UrlProvider(baseUrl) {

    companion object {
        const val CONFIRM_PAYMENT_ENDPOINT_FORMAT = "v2/payment/%s/confirm"
        const val GOOGLE_PAY_PAYMENT_ENDPOINT_FORMAT = "v2/google-pay/%s/start-payment"
        const val CONFIRM_PAYMENT_STATUS_ENDPOINT_FORMAT = "v2/payment/%s/status"
        const val CUSTOMERS_ENDPOINT = "v2/customers"
        const val RETRIEVE_CUSTOMER_VIA_MERCHANT_CUSTOMER_ID_ENDPOINT_FORMAT = "/v2/merchants/customers/%s"
        const val GET_PAYMENT_METHODS_FOR_CUSTOMER_URL_FORMAT = "$CUSTOMERS_ENDPOINT/%s/payment-methods?limit=%s&offset=%s"
    }

    fun getConfirmPaymentUrl(paymentId: String) = baseUrl + CONFIRM_PAYMENT_ENDPOINT_FORMAT.format(paymentId)

    fun getGooglePayPaymentUrl(paymentId: String) = baseUrl + GOOGLE_PAY_PAYMENT_ENDPOINT_FORMAT.format(paymentId)

    fun getPaymentStatusUrl(id: String) = baseUrl + CONFIRM_PAYMENT_STATUS_ENDPOINT_FORMAT.format(id)

    fun getCustomersBaseUrl() = baseUrl + CUSTOMERS_ENDPOINT

    fun getCustomerUrl(customerUuid: String) = "${getCustomersBaseUrl()}/$customerUuid"

    fun getRetrieveCustomerViaMerchantCustomerIdUrl(merchantCustomerUuid: String) = baseUrl + RETRIEVE_CUSTOMER_VIA_MERCHANT_CUSTOMER_ID_ENDPOINT_FORMAT.format(merchantCustomerUuid)

    fun getPaymentMethodForCustomerUrl(customerUuid: String, limit: Long, offset: Long) = baseUrl + GET_PAYMENT_METHODS_FOR_CUSTOMER_URL_FORMAT.format(customerUuid, limit, offset)
}