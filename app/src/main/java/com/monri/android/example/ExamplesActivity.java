package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ExamplesActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, ExamplesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples);

        this.<Button>findViewById(R.id.btn_payment_with_new_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.createIntent(ExamplesActivity.this));
            }
        });

        this.<Button>findViewById(R.id.btn_payment_with_saved_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PaymentMethodsActivity.createIntent(ExamplesActivity.this));
            }
        });
    }
}
