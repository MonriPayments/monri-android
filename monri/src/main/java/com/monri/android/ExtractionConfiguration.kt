package com.monri.android

import com.monri.android.ExtractionConfiguration.ImageType

data class ExtractionConfiguration(
    val imageConfiguration: ImageConfiguration = ImageConfiguration(),
    val extractionSettings: ExtractionSettings = ExtractionSettings.Builder().build()
) {
    enum class ImageType(@JvmField val value: String) {
        BASE64("base64");
    }
}

data class ImageConfiguration(
    val imageType: ImageType = ImageType.BASE64,
    val isImageCropped: Boolean = false
)

@ConsistentCopyVisibility
data class ExtractionSettings private constructor(
    val shouldReturnDocumentImage: Boolean,
    val skipDocumentSizeCheck: Boolean,
    val skipImageSizeCheck: Boolean,
    val canStoreImages: Boolean,
    val dontUseValidation: Boolean
) {

    class Builder {
        private var shouldReturnDocumentImage: Boolean = true
        private var skipDocumentSizeCheck: Boolean = false
        private var skipImageSizeCheck: Boolean = false
        private var canStoreImages: Boolean = false
        private var dontUseValidation: Boolean = false

        fun setShouldReturnDocumentImage(value: Boolean) = apply { this.shouldReturnDocumentImage = value }
        fun setSkipDocumentSizeCheck(value: Boolean) = apply { this.skipDocumentSizeCheck = value }
        fun setSkipImageSizeCheck(value: Boolean) = apply { this.skipImageSizeCheck = value }
        fun setCanStoreImages(value: Boolean) = apply { this.canStoreImages = value }
        fun setDontUseValidation(value: Boolean) = apply { this.dontUseValidation = value }

        fun build() = ExtractionSettings(
            shouldReturnDocumentImage = shouldReturnDocumentImage,
            skipDocumentSizeCheck = skipDocumentSizeCheck,
            skipImageSizeCheck = skipImageSizeCheck,
            canStoreImages = canStoreImages,
            dontUseValidation = dontUseValidation
        )
    }
}





