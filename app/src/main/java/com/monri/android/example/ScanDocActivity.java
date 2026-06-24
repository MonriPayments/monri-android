package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class ScanDocActivity extends FragmentActivity {

    public static Intent createIntent(final Context context) {
        return new Intent(context, ScanDocActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scandoc);

        ExtensionFunctionsKt.enableEdgeToEdge(this, findViewById(R.id.scandoc_fragment_container), false);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.scandoc_fragment_container, new ScanDocCameraPreviewFragment(), null)
                .commit();
    }
}

