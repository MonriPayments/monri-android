package com.monri.android.model

import com.monri.android.ExtractionConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocExtractRequestTest {

    companion object {
        private const val BASE_64_IMAGE = "base64image"
        private const val ACCEPT_TERMS_AND_CONDITIONS = true
    }

    @Test
    fun `toJSONObject should properly convert request to JSON`() {
        // given
        val expectedJSON = """{"AcceptTermsAndConditions":true,"DataFields":{"Image":"base64image","ImageType":"base64","ImageCropped":false},"Settings":{"ShouldReturnDocumentImage":true,"SkipDocumentSizeCheck":false,"SkipImageSizeCheck":false,"CanStoreImages":false,"DontUseValidation":false}}"""

        val extractionConfig = ExtractionConfiguration()
        val scanDocExtractRequest = ScanDocExtractRequest(BASE_64_IMAGE, ACCEPT_TERMS_AND_CONDITIONS, extractionConfig)

        // when
        val actualJSON = scanDocExtractRequest.toJSONObject().toString()

        // then
        assertThat(actualJSON).isEqualTo(expectedJSON)
    }
}