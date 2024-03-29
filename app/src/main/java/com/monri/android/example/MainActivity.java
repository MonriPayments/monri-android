package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.monri.android.Monri;
import com.monri.android.TokenCallback;
import com.monri.android.TokenRequest;
import com.monri.android.model.Card;
import com.monri.android.model.Token;
import com.monri.android.view.CardMultilineWidget;

public class MainActivity extends AppCompatActivity implements ViewDelegate {

    CardMultilineWidget widget;

    Button btnPay;

    TextView tvMainTrxStatus;

    CheckBox cbSaveCardForFuturePayments;

    OrderRepository orderRepository;

    public static Intent createIntent(Context context, PrepareTransactionResponse prepareTransactionResponse) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("PREPARE_TRANSACTION_RESPONSE", prepareTransactionResponse);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        widget = findViewById(R.id.card_multiline_widget);
        btnPay = findViewById(R.id.content_main_btn_pay);
        cbSaveCardForFuturePayments = findViewById(R.id.content_main_cb_save_card_for_future_payments);
        tvMainTrxStatus = findViewById(R.id.main_trx_status);


        orderRepository = new OrderRepository(this, this);
        final Monri monri = new Monri(((ActivityResultCaller) this), orderRepository.monriApiOptions());

        final PrepareTransactionResponse prepareTransactionResponse = getIntent().getParcelableExtra("PREPARE_TRANSACTION_RESPONSE");

        TokenRequest tokenRequest = new TokenRequest(prepareTransactionResponse.token, prepareTransactionResponse.digest, prepareTransactionResponse.timestamp);


        btnPay.setOnClickListener(v -> {
            v.setEnabled(false);

            final Card card = widget.getCard();

            if (card == null) {
                statusMessage("Card data invalid");

            } else {

                card.setTokenizePan(cbSaveCardForFuturePayments.isChecked());

                monri.createToken(tokenRequest, card, new TokenCallback() {
                    @Override
                    public void onSuccess(Token token) {
                        v.setEnabled(true);
                        orderRepository.order(token);
                    }

                    @Override
                    public void onError(Exception exception) {
                        v.setEnabled(true);
                        handleOrderFailure(exception);
                    }
                });
            }
        });

    }

    @Override
    public void statusMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        tvMainTrxStatus.setText(message);
    }

    void handleOrderFailure(Throwable throwable) {
        throwable.printStackTrace();
        statusMessage(throwable.getMessage());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        final Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
//        monriapp://example.monri.com/transaction-result?order_number=$id
        final String orderNumber = uri.getQueryParameter("order_number");
        statusMessage(String.format("Received result for %s", orderNumber));
        super.onNewIntent(intent);
    }
}
