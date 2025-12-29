package com.monri.android.model

import com.monri.android.ScanDocExtractionConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocExtractRequestTest {

    companion object {
        private const val BASE_64_IMAGE = "base64image"
        private const val ACCEPT_TERMS_AND_CONDITIONS = true
        private const val IMAGE_CROPPED = false
        private const val SHOULD_RETURN_DOCUMENT_IMAGE = true
        private const val SKIP_DOCUMENT_SIZE_CHECK = false
        private const val SKIP_IMAGE_SIZE_CHECK = false
        private const val CAN_STORE_IMAGES = false
        private const val DONT_USE_VALIDATION = false
    }

    @Test
    fun `toJSONObject should properly convert request to JSON`() {
        // given
        val expectedJSON = """{"AcceptTermsAndConditions":true,"DataFields":{"Image":"base64image","ImageType":"base64","ImageCropped":false},"Settings":{"ShouldReturnDocumentImage":true,"SkipDocumentSizeCheck":false,"SkipImageSizeCheck":false,"CanStoreImages":false,"DontUseValidation":false}}"""

        val extractionConfig = ScanDocExtractionConfig(
            ScanDocExtractionConfig.ImageType.BASE64,
            IMAGE_CROPPED,
            SHOULD_RETURN_DOCUMENT_IMAGE,
            SKIP_DOCUMENT_SIZE_CHECK,
            SKIP_IMAGE_SIZE_CHECK,
            CAN_STORE_IMAGES,
            DONT_USE_VALIDATION
            )
        val scanDocExtractRequest = ScanDocExtractRequest(BASE_64_IMAGE, ACCEPT_TERMS_AND_CONDITIONS, extractionConfig)

        // when
        val actualJSON = scanDocExtractRequest.toJSONObject().toString()

        // then
        assertThat(actualJSON).isEqualTo(expectedJSON)
    }
}