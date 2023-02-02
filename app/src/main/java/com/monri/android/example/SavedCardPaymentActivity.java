package com.monri.android.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;

import com.monri.android.Monri;
import com.monri.android.TokenCallback;
import com.monri.android.TokenRequest;
import com.monri.android.model.SavedCard;
import com.monri.android.model.Token;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SavedCardPaymentActivity extends AppCompatActivity implements ViewDelegate {

    @SuppressLint("SimpleDateFormat") private final DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static Intent createIntent(Context context,
                                      String panToken,
                                      String maskedPan,
                                      String cardType,
                                      PrepareTransactionResponse prepareTransactionResponse) {
        final Intent intent = new Intent(context, SavedCardPaymentActivity.class);
        intent.putExtra("PAN_TOKEN", panToken);
        intent.putExtra("MASKED_PAN", maskedPan);
        intent.putExtra("CARD_TYPE", cardType);
        intent.putExtra("PREPARE_TRANSACTION_RESPONSE", prepareTransactionResponse);
        return intent;
    }

    EditText etCvv;

    TextView tvCardType;
    TextView tvMaskedPan;
    Button btnPay;

    OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_card_payment);

        final Intent intent = getIntent();
        final String panToken = intent.getStringExtra("PAN_TOKEN");
        final String maskedPan = intent.getStringExtra("MASKED_PAN");
        final String cardType = intent.getStringExtra("CARD_TYPE");

        final PrepareTransactionResponse prepareTransactionResponse = getIntent().getParcelableExtra("PREPARE_TRANSACTION_RESPONSE");

        TokenRequest tokenRequest = new TokenRequest(prepareTransactionResponse.token, prepareTransactionResponse.digest, prepareTransactionResponse.timestamp);

        orderRepository = new OrderRepository(this, this);


        tvCardType = findViewById(R.id.tv_card_type);
        tvMaskedPan = findViewById(R.id.tv_masked_pan);
        etCvv = findViewById(R.id.et_cvv);
        btnPay = findViewById(R.id.btn_payment);

        tvCardType.setText(cardType);
        tvMaskedPan.setText(maskedPan);

//        Step one - instantiate monri
        final Monri monri = new Monri(((ActivityResultCaller) this), orderRepository.monriApiOptions());

        btnPay.setOnClickListener(v -> {
            SavedCard savedCard = new SavedCard(panToken, etCvv.getText().toString());
            monri.createToken(tokenRequest, savedCard, new TokenCallback() {
                @Override
                public void onSuccess(Token token) {
                    orderRepository.order(token);
                }

                @Override
                public void onError(Exception exception) {
                    orderRepository.handleOrderFailure(exception);
                }
            });
        });
    }

    @Override
    public void statusMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
