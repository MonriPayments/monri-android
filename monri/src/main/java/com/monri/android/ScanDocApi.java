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
            final List<Bitmap> scannedCardImages,
            final ValidationConfiguration validationConfiguration,
            final ResultCallback<ValidationResponse> resultCallback
    ) {

        final ScanDocValidateRequest request = new ScanDocValidateRequest(
                acceptTermsAndConditions,
                ImageProcessingUtil.imagesToBase64StringWithCompression(scannedCardImages),
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
        final String base64Image = ImageProcessingUtil.imageToBase64StringWithCompression(scannedCardImage);

        final ScanDocExtractRequest request = new ScanDocExtractRequest(
                base64Image,
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
