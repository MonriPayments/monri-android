package com.monri.android.model;

import static com.monri.android.json.JsonUtil.toList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ScanDocValidateResponse extends BaseScanDocResponse{
    private static final String KEYPOINTS_KEY = "Keypoints";
    private static final String DETECTED_BLUR_VALUE_KEY = "DetectedBlurValue";
    private static final String VALIDATED_KEY = "Validated";
    private static final String ANALYSIS_TIME_KEY = "AnalysisTime";
    public final List<ScanDocKeypoint> keypoints;
    public final double detectedBlurValue;
    public final boolean validated;
    public final String analysisTime;

    private ScanDocValidateResponse(final String transactionID,
                                   final String uploadedAt,
                                   final String productName,
                                   final List<String> errors,
                                   final List<String> warnings,
                                   final int status,
                                   final String method,
                                   final int infoCode,
                                   final List<ScanDocKeypoint> keypoints,
                                   final double detectedBlurValue,
                                   final boolean validated,
                                   final String analysisTime
    ) {
        super(transactionID, uploadedAt, productName, errors, warnings, status, method, infoCode);

        this.keypoints = keypoints;
        this.detectedBlurValue = detectedBlurValue;
        this.validated = validated;
        this.analysisTime = analysisTime;
    }

    public static ScanDocValidateResponse fromJSON(final JSONObject response) throws JSONException {

        return new ScanDocValidateResponse(
                response.getString(TRANSACTION_ID_KEY),
                response.getString(UPLOADED_AT_KEY),
                response.getString(PRODUCT_NAME_KEY),
                toList(response.getJSONArray(ERRORS_KEY)),
                toList(response.getJSONArray(WARNINGS_KEY)),
                response.getInt(STATUS_KEY),
                response.getString(METHOD_KEY),
                response.getInt(INFO_CODE_KEY),
                createKeypointList(response.getJSONArray(KEYPOINTS_KEY)),
                response.optDouble(DETECTED_BLUR_VALUE_KEY),
                response.getBoolean(VALIDATED_KEY),
                response.getString(ANALYSIS_TIME_KEY)
        );
    }

    private static List<ScanDocKeypoint> createKeypointList(final JSONArray jsonArray) throws JSONException {
        final List<ScanDocKeypoint> keypoints = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONArray currentCoordinates = jsonArray.getJSONArray(i);
            final ScanDocKeypoint keypoint = new ScanDocKeypoint(currentCoordinates.getDouble(0), currentCoordinates.getDouble(1));

            keypoints.add(keypoint);
        }

        return keypoints;
    }
}
