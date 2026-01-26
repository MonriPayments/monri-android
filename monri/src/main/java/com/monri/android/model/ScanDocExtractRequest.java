package com.monri.android.model;

import com.monri.android.ExtractionConfiguration;
import com.monri.android.ExtractionSettings;
import com.monri.android.ImageConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

public class ScanDocExtractRequest {
    private static final String IMAGE_KEY = "Image";
    private static final String IMAGE_TYPE_KEY = "ImageType";
    private static final String IMAGE_CROPPED_KEY = "ImageCropped";
    private static final String ACCEPT_TERMS_AND_CONDITIONS_KEY = "AcceptTermsAndConditions";
    private static final String SHOULD_RETURN_DOCUMENT_IMAGE_KEY = "ShouldReturnDocumentImage";
    private static final String SKIP_DOCUMENT_SIZE_CHECK_KEY = "SkipDocumentSizeCheck";
    private static final String SKIP_IMAGE_SIZE_CHECK_KEY = "SkipImageSizeCheck";
    private static final String CAN_STORE_IMAGES_KEY = "CanStoreImages";
    private static final String DONT_USE_VALIDATION_KEY = "DontUseValidation";
    private static final String DATA_FIELDS_KEY = "DataFields";
    private static final String SETTINGS_KEY = "Settings";
    private final boolean acceptTermsAndConditions;
    private final String base64Image;
    private final String imageType;
    private final boolean imageCropped;
    private final boolean shouldReturnDocumentImage;
    private final boolean skipDocumentSizeCheck;
    private final boolean skipImageSizeCheck;
    private final boolean canStoreImages;
    private final boolean dontUseValidation;

    public ScanDocExtractRequest(final String base64Image,
                                 final boolean acceptTermsAndConditions,
                                 final ExtractionConfiguration extractionConfig) {
        final ImageConfiguration imageConfiguration = extractionConfig.getImageConfiguration();
        final ExtractionSettings extractionSettings = extractionConfig.getExtractionSettings();

        this.acceptTermsAndConditions = acceptTermsAndConditions;
        this.base64Image = base64Image;
        this.imageType = imageConfiguration.getImageType().value;
        this.imageCropped = imageConfiguration.isImageCropped();
        this.shouldReturnDocumentImage = extractionSettings.getShouldReturnDocumentImage();
        this.skipDocumentSizeCheck = extractionSettings.getSkipDocumentSizeCheck();
        this.skipImageSizeCheck = extractionSettings.getSkipImageSizeCheck();
        this.canStoreImages = extractionSettings.getCanStoreImages();
        this.dontUseValidation = extractionSettings.getDontUseValidation();
    }

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject dataFieldsObject = new JSONObject().put(IMAGE_KEY, base64Image)
                                                            .put(IMAGE_TYPE_KEY, imageType)
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
