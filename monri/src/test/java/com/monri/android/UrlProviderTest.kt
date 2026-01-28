package com.monri.android

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

class UrlProviderTest {

    companion object {
        private const val BASE_URL = "BASEURL"
    }

    private val scanDocUrlProvider = ScanDocUrlProvider(BASE_URL)

    @Test
    fun `getAuthenticationUrl should return correct authentication url`() {
        // given
        val expectedUrl = "https://api.scandoc.ai/ks/authenticate/"

        // when
        val actualUrl = scanDocUrlProvider.authenticationUrl

        // then
        assertThat(actualUrl).isEqualTo(expectedUrl)
    }

    @Test
    fun `getExtractionUrl should return correct extraction url`() {
        // given
        val expectedUrl = "$BASE_URL/extraction/"

        // when
        val actualUrl = scanDocUrlProvider.extractionUrl

        // then
        assertThat(actualUrl).isEqualTo(expectedUrl)
    }

    @Test
    fun `getValidationUrl should return correct validation url`() {
        // given
        val expectedUrl = "$BASE_URL/validation/"

        // when
        val actualUrl = scanDocUrlProvider.validationUrl

        // then
        assertThat(actualUrl).isEqualTo(expectedUrl)
    }

    @Test
    fun `getAuthenticationRefreshUrl should return correct authentication refresh url`() {
        // given
        val expectedUrl = "https://api.scandoc.ai/ks/authenticate/refresh"

        // when
        val actualUrl = scanDocUrlProvider.authenticateRefreshUrl

        // then
        assertThat(actualUrl).isEqualTo(expectedUrl)
    }
}