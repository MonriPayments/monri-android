package com.monri.android;

import android.graphics.Bitmap;
import com.monri.android.model.ScanDocApiOptions;
import com.monri.android.model.ScanDocExtractRequest;
import com.monri.android.model.ExtractionResponse;
import com.monri.android.model.ScanDocValidateRequest;
import com.monri.android.model.ValidationResponse;
import java.util.List;

public class ScanDocApi {
    private final ScanDocHttpApiImpl scanDocHttpApiImpl;
    private final TaskRunner taskRunner;
    private final boolean acceptTermsAndConditions;

    public ScanDocApi(final ScanDocApiOptions scanDocApiOptions) {
        this.scanDocHttpApiImpl = new ScanDocHttpApiImpl(scanDocApiOptions);
        this.taskRunner = new TaskRunner();
        this.acceptTermsAndConditions = scanDocApiOptions.areTermsAndConditionsAccepted();
    }

    public void validateScannedCard(
            final Bitmap[] scannedCardImages,
            final ValidationConfiguration validationConfiguration,
            final ResultCallback<ValidationResponse> resultCallback
    ) {

        validateScannedCardInternal(
                ImageProcessingUtil.bitmapsToBase64StringWithCompression(scannedCardImages),
                validationConfiguration,
                resultCallback
        );
    }

    public void validateScannedCard(
            final List<byte[]> scannedCardImages,
            final ValidationConfiguration validationConfiguration,
            final ResultCallback<ValidationResponse> resultCallback
    ) {
        validateScannedCardInternal(
                ImageProcessingUtil.byteArraysToCompressedBase64Strings(scannedCardImages),
                validationConfiguration,
                resultCallback
        );
    }

    private void validateScannedCardInternal(
            final List<String> compressedBase64Images,
            final ValidationConfiguration validationConfiguration,
            final ResultCallback<ValidationResponse> resultCallback
    ) {
        final ScanDocValidateRequest request = new ScanDocValidateRequest(
                acceptTermsAndConditions,
                compressedBase64Images,
                validationConfiguration.getValidationSettings().getSkipImageSizeCheck(),
                validationConfiguration.getBlurValues()
        );

        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<ValidationResponse> result = scanDocHttpApiImpl.validateScannedCard(request);

                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                resultCallback
        );
    }

    public void extractDataFromScannedCard(
            final Bitmap scannedCardImage,
            final ResultCallback<ExtractionResponse> resultCallback
    ) {
        extractDataFromScannedCard(scannedCardImage, new ExtractionConfiguration(), resultCallback);
    }

    public void extractDataFromScannedCard(
            final Bitmap scannedCardImage,
            final ExtractionConfiguration extractionConfiguration,
            final ResultCallback<ExtractionResponse> resultCallback
    ) {
        extractDataFromScannedCardInternal(
                ImageProcessingUtil.bitmapToBase64WithCompression(scannedCardImage),
                extractionConfiguration,
                resultCallback
        );
    }

    public void extractDataFromScannedCard(
            final byte[] scannedCardImage,
            final ResultCallback<ExtractionResponse> resultCallback
    ) {
        extractDataFromScannedCard(scannedCardImage, new ExtractionConfiguration(), resultCallback);
    }

    public void extractDataFromScannedCard(
            final byte[] scannedCardImageAsBase64,
            final ExtractionConfiguration extractionConfiguration,
            final ResultCallback<ExtractionResponse> resultCallback
    ) {
        extractDataFromScannedCardInternal(
                ImageProcessingUtil.byteArrayToCompressedBase64String(scannedCardImageAsBase64),
                extractionConfiguration,
                resultCallback
        );
    }

    private void extractDataFromScannedCardInternal(
            final String compressedBase64Image,
            final ExtractionConfiguration extractionConfiguration,
            final ResultCallback<ExtractionResponse> resultCallback
    ) {
        final ScanDocExtractRequest request = new ScanDocExtractRequest(
                compressedBase64Image,
                acceptTermsAndConditions,
                extractionConfiguration
        );

        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<ExtractionResponse> result = scanDocHttpApiImpl.extractDataFromScannedCard(request);

                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                resultCallback
        );
    }
}
