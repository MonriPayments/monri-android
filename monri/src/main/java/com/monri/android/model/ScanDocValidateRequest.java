package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class ScanDocValidateRequest {
    private static final String IMAGES_KEY = "Images";
    private static final String BLUR_VALUES_KEY = "BlurValues";
    private static final String SKIP_IMAGE_SIZE_CHECK_KEY = "SkipImageSizeCheck";
    private static final String ACCEPT_TERMS_AND_CONDITIONS_KEY = "AcceptTermsAndConditions";
    private static final String DATA_FIELDS_KEY = "DataFields";
    private static final String SETTINGS_KEY = "Settings";
    private final boolean acceptTermsAndConditions;
    private final String base64EncodedImage;
    private final Boolean skipImageSizeCheck;
    private final List<String> blurValues;

    public ScanDocValidateRequest(final Boolean acceptTermsAndConditions, final String base64EncodedImage, final Boolean skipImageSizeCheck, final List<String> blurValues) {
        this.acceptTermsAndConditions = acceptTermsAndConditions;
        this.base64EncodedImage = base64EncodedImage;
        this.skipImageSizeCheck = skipImageSizeCheck;
        this.blurValues = blurValues;
    }

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject dataFieldsObject = new JSONObject().put(IMAGES_KEY, new JSONArray(base64EncodedImage))
                                                            .put(BLUR_VALUES_KEY, new JSONArray(blurValues));

        final JSONObject settingsObject = new JSONObject().put(SKIP_IMAGE_SIZE_CHECK_KEY, skipImageSizeCheck);

        return new JSONObject().put(ACCEPT_TERMS_AND_CONDITIONS_KEY, acceptTermsAndConditions)
                                                         .put(DATA_FIELDS_KEY, dataFieldsObject)
                                                         .put(SETTINGS_KEY, settingsObject);
    }
}
