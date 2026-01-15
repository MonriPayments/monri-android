package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocAuthenticateRequestTest {

    companion object {
        private const val USER_KEY = "userkey"
        private const val SUB_CLIENT = "subclient"
    }

    @Test
    fun `toJSONObject should properly convert request to JSON`() {
        // given
        val scanDocAuthenticateRequest = ScanDocAuthenticateRequest(USER_KEY.toByteArray(), SUB_CLIENT)
        val expectedJSON = """{"user_key":"userkey","sub_client":"subclient"}"""

        // when
        val actualJSON = scanDocAuthenticateRequest.toJSONObject().toString()

        // then
        assertThat(actualJSON).isEqualTo(expectedJSON)
    }
}