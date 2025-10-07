package com.monri.android.model;

import com.monri.android.ScanDocExtractionConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class ScanDocExtractRequest {
    private static final String IMAGE_TYPE_KEY = "imageType";
    private static final String IMAGE_CROPPED_KEY = "imageCroppped";
    private static final String ACCEPT_TERMS_AND_CONDITIONS_KEY = "AcceptTermsAndConditions";
    private static final String SHOULD_RETURN_DOCUMENT_IMAGE_KEY = "shouldReturnDocumentImage";
    private static final String SKIP_DOCUMENT_SIZE_CHECK_KEY = "skipDocumentSizeCheck";
    private static final String SKIP_IMAGE_SIZE_CHECK_KEY = "skipImageSizeCheck";
    private static final String CAN_STORE_IMAGES_KEY = "canStoreImages";
    private static final String DONT_USE_VALIDATION_KEY = "dontUseValidation";
    private static final String DATA_FIELDS_KEY = "DataFields";
    private static final String SETTINGS_KEY = "Settings";
    private final boolean acceptTermsAndConditions;
    private final String imageType;
    private final boolean imageCropped;
    private final boolean shouldReturnDocumentImage;
    private final boolean skipDocumentSizeCheck;
    private final boolean skipImageSizeCheck;
    private final boolean canStoreImages;
    private final boolean dontUseValidation;

    public ScanDocExtractRequest(final boolean acceptTermsAndConditions,
                                 final ScanDocExtractionConfig extractionConfig) {
        this.acceptTermsAndConditions = acceptTermsAndConditions;
        this.imageType = extractionConfig.getImageType();
        this.imageCropped = extractionConfig.isImageCropped();
        this.shouldReturnDocumentImage = extractionConfig.isShouldReturnDocumentImage();
        this.skipDocumentSizeCheck = extractionConfig.isSkipDocumentSizeCheck();
        this.skipImageSizeCheck = extractionConfig.isSkipImageSizeCheck();
        this.canStoreImages = extractionConfig.isCanStoreImages();
        this.dontUseValidation = extractionConfig.isDontUseValidation();
    }

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject dataFieldsObject = new JSONObject().put(IMAGE_TYPE_KEY, imageType)
                                                            .put(IMAGE_CROPPED_KEY, imageCropped);

        final JSONObject settingsObject = new JSONObject().put(SHOULD_RETURN_DOCUMENT_IMAGE_KEY, shouldReturnDocumentImage)
                                                          .put(SKIP_DOCUMENT_SIZE_CHECK_KEY, skipDocumentSizeCheck)
                                                          .put(SKIP_IMAGE_SIZE_CHECK_KEY, skipImageSizeCheck)
                                                          .put(CAN_STORE_IMAGES_KEY, canStoreImages)
                                                          .put(DONT_USE_VALIDATION_KEY, dontUseValidation);

        return new JSONObject().put(ACCEPT_TERMS_AND_CONDITIONS_KEY, acceptTermsAndConditions)
                               .put(DATA_FIELDS_KEY, dataFieldsObject)
                               .put(SETTINGS_KEY, settingsObject);
    }
}
