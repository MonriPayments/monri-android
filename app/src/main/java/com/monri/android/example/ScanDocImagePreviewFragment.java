package com.monri.android.example;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.monri.android.ResultCallback;
import com.monri.android.ScanDocApi;
import com.monri.android.ValidationConfiguration;
import com.monri.android.model.ScanDocApiOptions;
import com.monri.android.model.ExtractionResponse;
import com.monri.android.model.ValidationResponse;
import java.io.IOException;
import java.util.List;

public class ScanDocImagePreviewFragment extends Fragment {
    private static final String SCANDOC_BASE_URL = "BACKEND_BASE_URL";
    private static final String SCANDOC_USER_KEY = "USER_KEY";
    private static final String SCANDOC_SUBCLIENT = "androidTestClient";
    private static final String BUNDLE_PHOTO_URI_KEY = "photoUri";
    private static final boolean ACCEPT_SCANDOC_TERMS_AND_CONDITIONS = true;
    private ProgressBar progressBar;
    private Uri photoUri;
    private ScanDocApi scanDocApi;

    public static ScanDocImagePreviewFragment newInstance(final String photoUri) {
        final Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_PHOTO_URI_KEY, photoUri);

        final ScanDocImagePreviewFragment newInstance =  new ScanDocImagePreviewFragment();
        newInstance.setArguments(bundle);

        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scandoc_image_preview, container, false);

        getArgumentsFromBundle();
        initializeViews(view);
        initializeScanDocApi();

        return view;
    }

    private void initializeViews(final View rootView) {
        final Button validateButton = rootView.findViewById(R.id.button_validate);
        final ImageView imagePreview = rootView.findViewById(R.id.image_preview);
        final Button backButton = rootView.findViewById(R.id.button_retake);

        progressBar = rootView.findViewById(R.id.progress_bar);

        imagePreview.setImageURI(photoUri);

        validateButton.setOnClickListener(v -> validateImage());
        backButton.setOnClickListener(v -> goBack());
    }

    private void getArgumentsFromBundle() {
        final Bundle bundle = getArguments();

        if (bundle != null) {
            photoUri = Uri.parse(bundle.getString(BUNDLE_PHOTO_URI_KEY));
        }
    }

    private void initializeScanDocApi() {
        scanDocApi = new ScanDocApi(
                new ScanDocApiOptions(
                        SCANDOC_BASE_URL,
                        SCANDOC_USER_KEY,
                        SCANDOC_SUBCLIENT,
                        ACCEPT_SCANDOC_TERMS_AND_CONDITIONS
                )
        );
    }

    private void goBack() {
        getParentFragmentManager().popBackStack();
    }

    private void validateImage() {
        progressBar.setVisibility(View.VISIBLE);
        scanDocApi.validateScannedCard(new Bitmap[]{createBitmapFromUri()}, new ValidationConfiguration(), new ScanDocValidationCallback());
    }

    private void extractData() {
        progressBar.setVisibility(View.VISIBLE);
        scanDocApi.extractDataFromScannedCard(createBitmapFromUri(), new ScanDocExtractionCallback());
    }

    private class ScanDocValidationCallback implements ResultCallback<ValidationResponse> {
        @Override
        public void onSuccess(final ValidationResponse result) {
            progressBar.setVisibility(View.GONE);
            extractData();
        }

        @Override
        public void onError(final Throwable throwable) {
            progressBar.setVisibility(View.GONE);
            showErrorDialog(getString(R.string.scandoc_activity_validation_error_text, throwable.toString()));
        }
    }

    private void showErrorDialog(final String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.scandoc_activity_error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.scandoc_activity_error_dialog_ok_button_text, null)
                .show();
    }

    private Bitmap createBitmapFromUri() {
        Bitmap bitmap = null;

        try {
             bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
        } catch (final IOException e) {
            showErrorDialog(e.getMessage());
        }

        return bitmap;
    }

    private class ScanDocExtractionCallback implements ResultCallback<ExtractionResponse> {
        @Override
        public void onSuccess(final ExtractionResponse result) {
            final ScanDocExtractedDataFragment extractedDataFragment = ScanDocExtractedDataFragment.newInstance(result);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, extractedDataFragment)
                    .commit();
        }

        @Override
        public void onError(final Throwable throwable) {
            progressBar.setVisibility(View.GONE);
            showErrorDialog(getString(R.string.scandoc_activity_extraction_error_text, throwable.toString()));
        }
    }
}
