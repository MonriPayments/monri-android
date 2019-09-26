package com.monri.android.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.monri.android.Monri;
import com.monri.android.MonriTextUtils;
import com.monri.android.TokenCallback;
import com.monri.android.TokenRequest;
import com.monri.android.model.Card;
import com.monri.android.model.Token;
import com.monri.android.view.CardMultilineWidget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SimpleDateFormat") private final DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    CardMultilineWidget widget;

    Button payButton;

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        widget = findViewById(R.id.card_multiline_widget);
        payButton = findViewById(R.id.pay_button);


//        TODO: replace with your merchant's authenticity token
        String authenticityToken = "6a13d79bde8da9320e88923cb3472fb638619ccb";
//        TODO: replace with your merchant's merchant key
        final String merchantKey = "TestKeyXULLyvgWyPJSwOHe";

//        Step one - instantiate monri
        final Monri monri = new Monri(this.getApplicationContext(), authenticityToken);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String timestamp = isoFormat.format(new Date());
                final String token = UUID.randomUUID().toString();

                TokenRequest tokenRequest = new TokenRequest(token, MonriTextUtils.sha512HashInput(String.format("%s%s%s", merchantKey, token, timestamp)), timestamp);

                final Card card = widget.getCard();

                if (card == null) {
                    Toast.makeText(MainActivity.this, "Card data invalid", Toast.LENGTH_LONG).show();
                } else {

                    monri.createToken(tokenRequest, card, new TokenCallback() {
                        @Override
                        public void onSuccess(Token token) {
                            submitTokenToBackend(token);
                            Toast.makeText(MainActivity.this, String.format("Token successfully created\n%s", token.getId()), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Exception exception) {
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    private void submitTokenToBackend(Token token) {
        // Submit token to backend to charge card - authorization, purchase
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
}
