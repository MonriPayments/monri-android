package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    TextView txtViewResult;

    public static Intent createIntent(Context context) {
        return new Intent(context, PaymentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        txtViewResult = findViewById(R.id.txt_result_payment_example);

        findViewById(R.id.btn_payment_example).setOnClickListener(v -> newPayment(false, false));

        findViewById(R.id.btn_payment_example_3ds1).setOnClickListener(v -> newPayment(true, false));

        findViewById(R.id.btn_payment_example_save_card_for_future_payments).setOnClickListener(v -> newPayment(true, true));

        findViewById(R.id.btn_add_payment_method_example).setOnClickListener(v -> addPaymentMethod(false));
        findViewById(R.id.btn_add_payment_method_example_3ds1).setOnClickListener(v -> addPaymentMethod(true));

        findViewById(R.id.btn_payment_example_saved_card_non_3ds).setOnClickListener(v -> savedCardPayment(false));
        findViewById(R.id.btn_payment_example_saved_card_3ds).setOnClickListener(v -> savedCardPayment(true));

    }

    private void addPaymentMethod(boolean threeDS) {
        startActivity(PaymentPickerActivity.createIntent(this, threeDS, true, false));
    }

    private void newPayment(boolean threeDS, boolean saveCardForFuturePayments) {
        startActivity(PaymentPickerActivity.createIntent(this, threeDS, false, saveCardForFuturePayments));
    }

    private void savedCardPayment(boolean threeDS) {
        if (threeDS) {
            Intent intent = PaymentPickerActivity
                    .createIntent(this,
                            "c32b3465be7278d239f68bb6d7623acf0530bf34574cf3b782754d281c76bd02",
                            "434179******0044",
                            "visa"
                    );
            startActivity(intent);
        } else {
            Intent intent = PaymentPickerActivity
                    .createIntent(this,
                            "d5719409d1b8eb92adae0feccd2964b805f93ae3936fdd9d8fc01a800d094584",
                            "403530******4083",
                            "visa");
            startActivity(intent);
        }
    }

}
