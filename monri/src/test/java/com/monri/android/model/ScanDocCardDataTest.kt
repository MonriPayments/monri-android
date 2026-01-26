package com.monri.android.model

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanDocCardDataTest {

    @Test
    fun `fromJSON should parse JSON into ScanDocCardData object`() {
        // given
        val responseJSON = JSONObject(
            """
            {
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
                  "Value": "('VISA', 'NAME SURNAME', '12/20', '4000', '4000', '1234', '5678', '9010')"
                },
                "LuhnCheck": {
                  "Read": true,
                  "Value": "FALSE"
                }
              }
            }
            """.trimIndent()
        )

        // when
        val cardData = ScanDocCardData.fromJSON(responseJSON.getJSONObject("Data"))

        // then
        assertThat("12/20").isEqualTo(cardData.expiryDate)
        assertThat("('VISA', 'NAME SURNAME', '12/20', '4000', '4000', '1234', '5678', '9010')").isEqualTo(cardData.extractedTexts)
        assertThat("NAME SURNAME").isEqualTo(cardData.holdersName)
        assertThat(cardData.iban).isNull()
        assertThat(cardData.issuedDate).isNull()
        assertThat("FALSE").isEqualTo(cardData.luhnCheck)
        assertThat("4000123456789010").isEqualTo(cardData.cardNumber)
    }

    @Test
    fun `fromJSON should throw exception if data field is missing`() {
        // given
        val responseJSON = JSONObject(
            """
            {
              "Data": {
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
                  "Value": "('VISA', 'NAME SURNAME', '12/20', '4000', '4000', '1234', '5678', '9010')"
                },
                "LuhnCheck": {
                  "Read": true,
                  "Value": "FALSE"
                }
              }
            }
            """.trimIndent()
        )

        // when

        // then
        assertThrows(JSONException::class.java) {
            ValidationResponse.fromJSON(responseJSON)
        }
    }
}