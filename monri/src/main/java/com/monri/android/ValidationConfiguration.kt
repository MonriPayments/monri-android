package com.monri.android

data class ValidationConfiguration(
    val validationSettings: ValidationSettings = ValidationSettings(),
    var blurValues: List<Double> = emptyList()
)

data class ValidationSettings(
    val skipImageSizeCheck: Boolean = false
)
