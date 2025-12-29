package com.monri.android.model
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocRefreshAccessTokenRequestTest {

    companion object {
        private const val REFRESH_TOKEN = "refreshToken"
    }

    @Test
    fun `toJSONObject should properly convert request to JSON`() {
        // given
        val expectedJSON = """{"refresh_token":"refreshToken"}"""
        val scanDocRefreshAccessTokenRequest = ScanDocRefreshAccessTokenRequest(REFRESH_TOKEN)

        // when
        val actualJSON = scanDocRefreshAccessTokenRequest.toJSONObject().toString()

        // then
        assertThat(actualJSON).isEqualTo(expectedJSON)
    }
}