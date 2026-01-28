package com.monri.android

internal sealed interface HttpsRequest {

    val typeName: String
    val url: String
    val headers: Map<String, String>

    data class Post(
        override val url: String,
        override val headers: Map<String, String>,
        @JvmField val body: String? = null,
        @JvmField val useChunkedStreamingMode: Boolean = false
    ): HttpsRequest {
        override val typeName = "POST"
    }

    data class Get(
        override val url: String,
        override val headers: Map<String, String>
    ): HttpsRequest {
        override val typeName = "GET"
    }

    data class Delete(
        override val url: String,
        override val headers: Map<String, String>
    ): HttpsRequest {
        override val typeName = "DELETE"
    }
}