package com.monri.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ProgressBar;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.Nullable;

import com.monri.android.BuildConfig;
import com.monri.android.Monri;
import com.monri.android.MonriUtil;
import com.monri.android.R;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;

public class ConfirmPaymentActivity extends ComponentActivity {

    private static final String CONFIRM_PAYMENT_PARAMS_BUNDLE = "CONFIRM_PAYMENT_PARAMS_BUNDLE";
    private static final String MONRI_API_OPTIONS = "MONRI_API_OPTIONS";
    Monri monri;

    PaymentAuthWebView webView;
    ProgressBar progressBar;

    /**
     * @deprecated use {@link #createIntent(Context context, Request input)}
     */
    public static Intent createIntent(Context context, ConfirmPaymentParams params, MonriApiOptions apiOptions) {
        final Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE, params);
        intent.putExtra(MONRI_API_OPTIONS, apiOptions);
        return intent;
    }

    @Nullable
    public static Response parseResponse(int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentResult paymentResult = intent.getParcelableExtra(PaymentResult.BUNDLE_NAME);
            return new Response(paymentResult);
        }
        return null;
    }

    public static Intent createIntent(Context context, Request input) {
        final Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE, input.params);
        intent.putExtra(MONRI_API_OPTIONS, input.apiOptions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        webView = findViewById(R.id.web_view_confirm_payment);
        progressBar = findViewById(R.id.progress_bar_confirm_payment);

        final ConfirmPaymentParams confirmPaymentParams = getIntent().getParcelableExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE);
        final MonriApiOptions apiOptions = getIntent().getParcelableExtra(MONRI_API_OPTIONS);

        confirmPaymentParams
                .getTransaction()
                .set("meta.integration_type", "android-sdk")
                .set("meta.library", MonriUtil.library(getApplicationContext()))
                .set("meta.library_version", BuildConfig.MONRI_SDK_VERSION);

        monri = new Monri(((ActivityResultCaller) this), apiOptions);

        ConfirmPaymentResponseCallback responseCallback = ConfirmPaymentResponseCallback.create(this, webView, progressBar, confirmPaymentParams, monri.getMonriApi());

        monri.getMonriApi().confirmPayment(confirmPaymentParams, responseCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        monri = null;
    }

    public static class Response implements Parcelable {
        PaymentResult paymentResult;

        public Response(PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
        }

        protected Response(Parcel in) {
            paymentResult = in.readParcelable(PaymentResult.class.getClassLoader());
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(paymentResult, flags);
        }

        public PaymentResult getPaymentResult() {
            return paymentResult;
        }
    }

    public static class Request implements Parcelable {
        ConfirmPaymentParams params;
        MonriApiOptions apiOptions;

        public Request(ConfirmPaymentParams params, MonriApiOptions apiOptions) {
            this.params = params;
            this.apiOptions = apiOptions;
        }

        protected Request(Parcel in) {
            params = in.readParcelable(ConfirmPaymentParams.class.getClassLoader());
            apiOptions = in.readParcelable(MonriApiOptions.class.getClassLoader());
        }

        public static final Creator<Request> CREATOR = new Creator<Request>() {
            @Override
            public Request createFromParcel(Parcel in) {
                return new Request(in);
            }

            @Override
            public Request[] newArray(int size) {
                return new Request[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(params, flags);
            dest.writeParcelable(apiOptions, flags);
        }
    }
}
