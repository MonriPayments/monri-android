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
    }

    private void addPaymentMethod(boolean threeDS) {
        startActivity(PaymentPickerActivity.createIntent(this, threeDS, true, false));
    }

    private void newPayment(boolean threeDS, boolean saveCardForFuturePayments) {
        startActivity(PaymentPickerActivity.createIntent(this, threeDS, false, saveCardForFuturePayments));
    }

}
