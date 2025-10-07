package com.monri.android;

public class ScanDocExtractionConfig {
    private final String imageType;
    private final boolean imageCropped;
    private final boolean shouldReturnDocumentImage;
    private final boolean skipDocumentSizeCheck;
    private final boolean skipImageSizeCheck;
    private final boolean canStoreImages;
    private final boolean dontUseValidation;

    public ScanDocExtractionConfig(String imageType,
                                   boolean imageCropped,
                                   boolean shouldReturnDocumentImage,
                                   boolean skipDocumentSizeCheck,
                                   boolean skipImageSizeCheck,
                                   boolean canStoreImages,
                                   boolean dontUseValidation) {
        this.imageType = imageType;
        this.imageCropped = imageCropped;
        this.shouldReturnDocumentImage = shouldReturnDocumentImage;
        this.skipDocumentSizeCheck = skipDocumentSizeCheck;
        this.skipImageSizeCheck = skipImageSizeCheck;
        this.canStoreImages = canStoreImages;
        this.dontUseValidation = dontUseValidation;
    }

    public String getImageType() {
        return imageType;
    }

    public boolean isDontUseValidation() {
        return dontUseValidation;
    }

    public boolean isCanStoreImages() {
        return canStoreImages;
    }

    public boolean isSkipDocumentSizeCheck() {
        return skipDocumentSizeCheck;
    }

    public boolean isShouldReturnDocumentImage() {
        return shouldReturnDocumentImage;
    }

    public boolean isImageCropped() {
        return imageCropped;
    }

    public boolean isSkipImageSizeCheck() {
        return skipImageSizeCheck;
    }
}
