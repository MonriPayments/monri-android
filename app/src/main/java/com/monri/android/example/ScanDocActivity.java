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

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new ScanDocCameraPreviewFragment(), null)
                .commit();
    }
}

