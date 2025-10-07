package com.monri.android.model;

import static com.monri.android.json.JsonUtil.toList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ScanDocExtractResponse extends BaseScanDocResponse {
    private static final String DATA_KEY = "Data";
    private static final String IMAGE_DATA_KEY = "ImageData";
    private static final String CREDIT_CARD_IMAGE_KEY = "CreditCardImage";
    private static final String ANALYSIS_TIME_KEY = "AnalysisTime";
    private final String base64CreditCardImage;
    private final String analysisTime;
    private final ScanDocCardData cardData;

    private ScanDocExtractResponse(final String transactionID,
                                   final String uploadedAt,
                                   final String productName,
                                   final List<String> errors,
                                   final List<String> warnings,
                                   final int status,
                                   final String method,
                                   final int infoCode,
                                   final String base64CreditCardImage,
                                   final String analysisTime,
                                   final ScanDocCardData cardData
    ) {
        super(transactionID, uploadedAt, productName, errors, warnings, status, method, infoCode);
        this.base64CreditCardImage = base64CreditCardImage;
        this.analysisTime = analysisTime;
        this.cardData = cardData;
    }

    public static ScanDocExtractResponse fromJSON(final JSONObject response) throws JSONException {
        return new ScanDocExtractResponse(response.getString(TRANSACTION_ID_KEY),
                                          response.getString(UPLOADED_AT_KEY),
                                          response.getString(PRODUCT_NAME_KEY),
                                          toList(response.getJSONArray(ERRORS_KEY)),
                                          toList(response.getJSONArray(WARNINGS_KEY)),
                                          response.getInt(STATUS_KEY),
                                          response.getString(METHOD_KEY),
                                          response.getInt(INFO_CODE_KEY),
                                          response.getJSONObject(IMAGE_DATA_KEY).getString(CREDIT_CARD_IMAGE_KEY),
                                          response.getString(ANALYSIS_TIME_KEY),
                                          ScanDocCardData.fromJSON(response.getJSONObject(DATA_KEY)));
    }
}
