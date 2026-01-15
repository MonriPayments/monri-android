package com.monri.android

import com.monri.android.model.AccessToken
import org.json.JSONException
import kotlin.jvm.Throws

interface ScanDocAuthenticationManager {

    @Throws(JSONException::class)
    fun authenticate(): AccessToken
}