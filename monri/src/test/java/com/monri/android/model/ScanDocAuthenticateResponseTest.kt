package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocAuthenticateResponseTest {

    @Test
    fun `fromJSON should parse JSON to ScanDocAuthenticateResponse object`() {
        // given
        val responseJSON = JSONObject(
            """
            {
                "access_token": "accessTokenValue",
                "refresh_token": "refreshTokenValue"
            }
          """
        )

        // when
        val scanDocAuthenticateResponse = ScanDocAuthenticateResponse.fromJSON(responseJSON)

        // then
        assertThat(scanDocAuthenticateResponse.accessToken).isEqualTo("accessTokenValue")
        assertThat(scanDocAuthenticateResponse.refreshToken).isEqualTo("refreshTokenValue")
    }

    @Test
    fun `fromJSON should parse JSON to ScanDocAuthenticateResponse object successfully when refresh token is null`() {
        // given
        val responseJSON = JSONObject(
            """
            {
                "access_token": "refreshedAccessTokenValue"
            }
          """
        )

        // when
        val scanDocAuthenticateResponse = ScanDocAuthenticateResponse.fromJSON(responseJSON)

        // then
        assertThat(scanDocAuthenticateResponse.accessToken).isEqualTo("refreshedAccessTokenValue")
        assertThat(scanDocAuthenticateResponse.refreshToken).isNull()
    }
}