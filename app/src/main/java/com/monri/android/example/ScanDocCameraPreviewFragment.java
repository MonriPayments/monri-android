package com.monri.android.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;

public class ScanDocCameraPreviewFragment extends Fragment {
    private static final String FILE_NAME_FORMAT = "CameraX-%d.jpg";
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private PreviewView cameraPreviewView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) initializeCameraXObjects();
            else showCameraPermissionRequiredMessage();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scandoc_camera_preview, container, false);

        initializeViews(view);
        checkPermissions();

        return view;
    }

    private void initializeViews(final View rootView) {
        final ImageButton captureButton = rootView.findViewById(R.id.capture_button);
        cameraPreviewView = rootView.findViewById(R.id.camera_preview);

        captureButton.setOnClickListener(v -> captureImage());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initializeCameraXObjects();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showCameraPermissionRequiredMessage() {
        Toast.makeText(getActivity(), R.string.scandoc_activity_camera_permission_required, Toast.LENGTH_LONG).show();
    }

    private void initializeCameraXObjects() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraXObjects();
            } catch (final Exception e) {
                showErrorDialog(e.getMessage());
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    private void bindCameraXObjects() {
        final Preview preview = new Preview.Builder().build();

        final CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().setTargetRotation(cameraPreviewView.getDisplay().getRotation()).build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
    }

    private void captureImage() {
        final File photoFile = new File(getActivity().getExternalMediaDirs()[0], String.format(FILE_NAME_FORMAT, System.currentTimeMillis()));
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(getActivity()), new ImageSavedCallback(photoFile));
    }

    private void showErrorDialog(final String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.scandoc_activity_error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.scandoc_activity_error_dialog_ok_button_text, null)
                .show();
    }

    private class ImageSavedCallback implements ImageCapture.OnImageSavedCallback {
        final File photofile;

        public ImageSavedCallback(final File photofile) {
            this.photofile = photofile;
        }

        @Override
        public void onImageSaved(@NonNull final ImageCapture.OutputFileResults outputFileResults) {
            final String base64Image = ImageProcessingUtil.imageToBase64StringWithCompression(photofile, 70);

            final ScanDocImagePreviewFragment scanDocImagePreviewFragment = ScanDocImagePreviewFragment.newInstance(
                    base64Image,
                    Uri.fromFile(photofile).toString());

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, scanDocImagePreviewFragment)
                    .addToBackStack(null)
                    .commit();
        }

        @Override
        public void onError(final @NonNull ImageCaptureException exception) {
            showErrorDialog(exception.getMessage());
        }
    }
}
