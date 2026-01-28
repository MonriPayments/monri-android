package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocValidateRequestTest {

    companion object {
        private const val ACCEPT_TERMS_AND_CONDITIONS = true
        private const val BASE_64_ENCODED_IMAGE_1 = "base64Image"
        private const val BASE_64_ENCODED_IMAGE_2 = "anotherBase64Image"
        private const val SKIP_IMAGE_SIZE_CHECK = false
        private val blurValues = listOf<Double>(12.3, 2345.21, 34.76)
    }

    @Test
    fun `toJSONObject should properly convert request to JSON`() {
        // given
        val expectedJSON = """{"AcceptTermsAndConditions":true,"DataFields":{"Images":["base64Image","anotherBase64Image"],"BlurValues":[12.3,2345.21,34.76]},"Settings":{"SkipImageSizeCheck":false}}"""
        val scanDocValidateRequest = ScanDocValidateRequest(ACCEPT_TERMS_AND_CONDITIONS, listOf(BASE_64_ENCODED_IMAGE_1, BASE_64_ENCODED_IMAGE_2), SKIP_IMAGE_SIZE_CHECK, blurValues)

        // when
        val actualJSON = scanDocValidateRequest.toJSONObject().toString()

        // then
        assertThat(actualJSON).isEqualTo(expectedJSON)
    }

    @Test
    fun `toJSONObject should properly convert request to JSON when blur values are empty`() {
        // given
        val emptyBlurValues = listOf<Double>()
        val expectedJSON = """{"AcceptTermsAndConditions":true,"DataFields":{"Images":["base64Image"]},"Settings":{"SkipImageSizeCheck":false}}"""
        val scanDocValidateRequest = ScanDocValidateRequest(ACCEPT_TERMS_AND_CONDITIONS, listOf(BASE_64_ENCODED_IMAGE_1), SKIP_IMAGE_SIZE_CHECK, emptyBlurValues)

        // when
        val actualJSON = scanDocValidateRequest.toJSONObject().toString()

        // then
        assertThat(actualJSON).isEqualTo(expectedJSON)
    }

}