package com.monri.android.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.monri.android.model.Card;
import com.monri.android.CardWidget;
import com.monri.android.R;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public class CardMultilineWidget extends FrameLayout implements CardWidget {

    private static final int DEFAULT_ID = R.id.default_id;

    EditText cardNumberEditText;
    EditText cvvNumberEditText;
    EditText expDateEditText;
    ImageView cardIconImageView;

    public CardMultilineWidget(@NonNull Context context) {
        this(context, null);
    }

    public CardMultilineWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardMultilineWidget(@NonNull Context context, @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CardMultilineWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(getContext(), R.layout.card_multiline_widget, this);

        // This ensures that onRestoreInstanceState is called
        // during rotations.
        if (getId() == NO_ID) {
            setId(DEFAULT_ID);
        }

        cardNumberEditText = findViewById(R.id.ev_card_number);
        cvvNumberEditText = findViewById(R.id.et_cvv_number);
        expDateEditText = findViewById(R.id.et_expiration_date);
        cardIconImageView = findViewById(R.id.iv_card_icon);

        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cardNumberEditText.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER);
            expDateEditText.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE);
            cvvNumberEditText.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE);
        }

        // TODO: add accessibility support
        // TODO: add support for styling
    }

    @Override
    public Card getCard() {
        expDateEditText.getText()
        return new Card(cardNumberEditText.getText(), );
    }
}
