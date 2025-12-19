package com.monri.android;

public class ScanDocExtractionConfig {
    private final ImageType imageType;
    private final boolean imageCropped;
    private final boolean shouldReturnDocumentImage;
    private final boolean skipDocumentSizeCheck;
    private final boolean skipImageSizeCheck;
    private final boolean canStoreImages;
    private final boolean dontUseValidation;

    public ScanDocExtractionConfig(final ImageType imageType,
                                   final boolean imageCropped,
                                   final boolean shouldReturnDocumentImage,
                                   final boolean skipDocumentSizeCheck,
                                   final boolean skipImageSizeCheck,
                                   final boolean canStoreImages,
                                   final boolean dontUseValidation) {
        this.imageType = imageType;
        this.imageCropped = imageCropped;
        this.shouldReturnDocumentImage = shouldReturnDocumentImage;
        this.skipDocumentSizeCheck = skipDocumentSizeCheck;
        this.skipImageSizeCheck = skipImageSizeCheck;
        this.canStoreImages = canStoreImages;
        this.dontUseValidation = dontUseValidation;
    }

    public enum ImageType {
        BASE64("base64");

        private final String value;

        ImageType(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public ImageType getImageType() {
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
