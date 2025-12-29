package com.monri.android;

import com.monri.android.model.ScanDocApiOptions;
import com.monri.android.model.ScanDocExtractRequest;
import com.monri.android.model.ScanDocExtractResponse;
import com.monri.android.model.ScanDocValidateRequest;
import com.monri.android.model.ScanDocValidateResponse;
import java.util.List;

public class ScanDocApi {
    private static final Boolean TERMS_AND_CONDITIONS_ACCEPTED = true;
    private final ScanDocApiOptions scanDocApiOptions;
    private final ScanDocHttpApiImpl scanDocHttpApiImpl;
    private final TaskRunner taskRunner;

    public ScanDocApi(final ScanDocApiOptions scanDocApiOptions) {
        this.scanDocApiOptions = scanDocApiOptions;
        this.scanDocHttpApiImpl = new ScanDocHttpApiImpl(scanDocApiOptions);
        this.taskRunner = new TaskRunner();
    }

    public void validateScannedCard(
            final List<String> base64Images,
            final Boolean skipImageSizeCheck,
            final ResultCallback<ScanDocValidateResponse> resultCallback,
            final List<Double> blurValues
    ) {
        final ScanDocValidateRequest request = new ScanDocValidateRequest(
                TERMS_AND_CONDITIONS_ACCEPTED,
                base64Images,
                skipImageSizeCheck,
                blurValues
        );

        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<ScanDocValidateResponse> result = scanDocHttpApiImpl.validateScannedCard(request);

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
            final String base64Image,
            final ScanDocExtractionConfig scanDocExtractionConfig,
            final ResultCallback<ScanDocExtractResponse> resultCallback
    ) {
        final ScanDocExtractRequest request = new ScanDocExtractRequest(
                base64Image,
                TERMS_AND_CONDITIONS_ACCEPTED,
                scanDocExtractionConfig
        );

        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<ScanDocExtractResponse> result = scanDocHttpApiImpl.extractDataFromScannedCard(request);

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
