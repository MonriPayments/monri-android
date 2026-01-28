package com.monri.android

import org.json.JSONException
import org.json.JSONObject

internal fun interface JsonResponseMapper<T> {

    @Throws(JSONException::class)
    fun map(json: JSONObject): T
}