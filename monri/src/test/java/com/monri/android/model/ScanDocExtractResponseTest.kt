package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocExtractResponseTest {

    @Test
    fun `fromJSON should parse JSON to ScanDocExtractResponse object`() {
        // given
        val responseJSON = JSONObject(""" 
            {
              "TransactionID": "96cba490-eb50-4bf6-bb91-91d89521c6dd",
              "UploadedAt": "2025-12-29T15:29:19.441956",
              "ProductName": "Credit Card Scanning v1.0.0",
              "Errors": [],
              "Warnings": [],
              "Status": 200,
              "Method": "Extraction",
              "InfoCode": "1000",
              "AnalysisTime": 1592.884,
              "OS": null,
              "Browser": null,
              "Device": null,
              "Data": {
                "HoldersName": {
                  "Read": true,
                  "Value": "NAME SURNAME"
                },
                "CardNumber": {
                  "Read": true,
                  "Value": "4000123456789010"
                },
                "IssuedDate": {
                  "Read": false,
                  "Value": null
                },
                "IBAN": {
                  "Read": false,
                  "Value": null
                },
                "ExpiryDate": {
                  "Read": true,
                  "Value": "12/20"
                },
                "ExtractedTexts": {
                  "Read": true,
                  "Value": "('VISA', 'NAME SURNAME', '12/20', '4000', '1234', '4000', '5678', '9010', ')', 'VISA CLASSIC')"
                },
                "LuhnCheck": {
                  "Read": true,
                  "Value": "FALSE"
                }
              },
              "ImageData": {
                "CreditCardImage": "image"
              }
            }
        """.trimIndent())

        // when
        val scanDocExtractResponse = ScanDocExtractResponse.fromJSON(responseJSON)

        // then
        assertThat("image").isEqualTo(scanDocExtractResponse.base64CreditCardImage)
        assertThat(200).isEqualTo(scanDocExtractResponse.status)
        assertThat("NAME SURNAME").isEqualTo(scanDocExtractResponse.cardData.holdersName)
        assertThat(scanDocExtractResponse.browserInfo).isNull()
    }
}