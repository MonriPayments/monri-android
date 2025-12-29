package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocValidateResponseTest {


    @Test
    fun `fromJSON should parse JSON to ScanDocValidateResponseObject`() {
        // given
        val responseJSON = JSONObject("""
            {
              "TransactionID": "07cdfb58-0c20-4d91-8ec5-8bb4c248b7a9",
              "UploadedAt": "2025-12-29T13:09:22.402808",
              "ProductName": "Credit Card Scanning v1.0.0",
              "Errors": [],
              "Warnings": [],
              "Status": 200,
              "Method": "Validation",
              "InfoCode": "1009",
              "AnalysisTime": 256.883,
              "Keypoints": [
                [0.17940229177474976, 0.3670715093612671],
                [0.818016767501831, 0.37768155336380005],
                [0.8468124866485596, 0.6917765140533447],
                [0.15761470794677734, 0.6945531964302063]
              ],
              "Validated": false,
              "Index": -1,
              "DetectedBlurValue": 148.615478515625,
              "Info": "Waiting for camera to focus."
            }
            """.trimIndent())

        // when
        val response = ScanDocValidateResponse.fromJSON(responseJSON)

        // then
        assertThat("07cdfb58-0c20-4d91-8ec5-8bb4c248b7a9").isEqualTo(response.transactionID)
        assertThat("256.883").isEqualTo(response.analysisTime)
        assertThat(1009).isEqualTo(response.infoCode)
        assertThat(0.37768155336380005).isEqualTo(response.keypoints[1].y)
        assertThat(0.15761470794677734).isEqualTo(response.keypoints[3].x)
    }

    @Test
    fun `fromJSON should parse JSON to ScanDocValidateResponseObject when blur values are null`() {
        // given
        val responseJSON = JSONObject("""
            {
              "TransactionID": "07cdfb58-0c20-4d91-8ec5-8bb4c248b7a9",
              "UploadedAt": "2025-12-29T13:09:22.402808",
              "ProductName": "Credit Card Scanning v1.0.0",
              "Errors": [],
              "Warnings": [],
              "Status": 200,
              "Method": "Validation",
              "InfoCode": "1009",
              "AnalysisTime": 256.883,
              "Keypoints": [
                [0.17940229177474976, 0.3670715093612671],
                [0.818016767501831, 0.37768155336380005],
                [0.8468124866485596, 0.6917765140533447],
                [0.15761470794677734, 0.6945531964302063]
              ],
              "Validated": false,
              "Index": -1,
              "Info": "Waiting for camera to focus."
            }
            """.trimIndent())

        // when
        val response = ScanDocValidateResponse.fromJSON(responseJSON)

        // then
        assertThat(response.detectedBlurValue).isNaN()
    }
}
