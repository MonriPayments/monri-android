package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodsActivity extends AppCompatActivity {

    private PrepareTransactionResponse prepareTransactionResponse;

    public static Intent createIntent(Context context, PrepareTransactionResponse prepareTransactionResponse) {
        final Intent intent = new Intent(context, PaymentMethodsActivity.class);
        intent.putExtra("PREPARE_TRANSACTION_RESPONSE", prepareTransactionResponse);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        this.prepareTransactionResponse = getIntent().getParcelableExtra("PREPARE_TRANSACTION_RESPONSE");

        this.<Button>findViewById(R.id.btn_payment_method_1).setOnClickListener(v -> {
            final Intent intent = intentForID(v.getId());
            startActivity(intent);
        });

        this.<Button>findViewById(R.id.btn_payment_method_2).setOnClickListener(v -> {
            final Intent intent = intentForID(v.getId());
            startActivity(intent);
        });
    }

    Intent intentForID(@IdRes int id) {
        if (id == R.id.btn_payment_method_1) {
            return SavedCardPaymentActivity
                    .createIntent(PaymentMethodsActivity.this,
                                  "cafb28787e42aadcd73a7e92e5e57fa2b504280b40a26e75c00c62ec4c6f0a15",
                                  "440960******2830",
                                  "visa",
                                  prepareTransactionResponse
                    );
        } else if (id == R.id.btn_payment_method_2) {
            return SavedCardPaymentActivity
                    .createIntent(PaymentMethodsActivity.this,
                                  "d5719409d1b8eb92adae0feccd2964b805f93ae3936fdd9d8fc01a800d094584",
                                  "403530******4083",
                                  "visa",
                                  prepareTransactionResponse);
        } else {
            throw new IllegalArgumentException("Unknown id");
        }
    }
}
