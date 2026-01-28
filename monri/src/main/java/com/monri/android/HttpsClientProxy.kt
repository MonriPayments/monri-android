package com.monri.android

internal interface HttpsClientProxy {

    fun <T> doPostRequest(url: String, headers: Map<String, String>, body: String?, useChunkedStreamingMode: Boolean, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T>

    fun <T> doGetRequest(url: String, headers: Map<String, String>, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T>

    fun <T> doDeleteRequest(url: String, headers: Map<String, String>, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T>

    fun <T> doHttpsRequest(httpsRequest: HttpsRequest, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T>
}