package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocAuthenticateRequestTest {

    companion object {
        private const val USER_KEY_KEY = "user_key"
        private const val SUB_CLIENT_KEY = "sub_client"
        private const val USER_KEY_VALUE = "userkey"
        private const val SUB_CLIENT_VALUE = "subclient"
    }

    @Test
    fun `toJSONObject should properly convert request to JSON`() {
        // given
        val scanDocAuthenticateRequest = ScanDocAuthenticateRequest(USER_KEY_VALUE.toByteArray(), SUB_CLIENT_VALUE)
        val expectedJSON = """{"$USER_KEY_KEY":"$USER_KEY_VALUE","$SUB_CLIENT_KEY":"$SUB_CLIENT_VALUE"}"""

        // when
        val actualJSON = scanDocAuthenticateRequest.toJSONObject()

        // then
        assertThat(actualJSON.getString(USER_KEY_KEY)).isEqualTo(USER_KEY_VALUE)
        assertThat(actualJSON.getString(SUB_CLIENT_KEY)).isEqualTo(SUB_CLIENT_VALUE)
    }
}