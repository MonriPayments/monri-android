package com.monri.android.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.button.ButtonConstants;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.gms.wallet.contract.TaskResultContracts;
import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.google_pay.MonriGooglePaymentRequestHelper;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.GooglePayPayment;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.TransactionParams;
import org.json.JSONException;
import org.json.JSONObject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("SetTextI18n")
public class GooglePayActivity extends AppCompatActivity implements ViewDelegate {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private OrderRepository orderRepository;
    private Monri monri;
    private PaymentsClient googlePaymentsClient;
    private PayButton googlePayButton;
    private ProgressBar progressBar;
    private JSONObject googlePaymentMethodData;
    private TextView resultTextView;
    private NewPaymentResponse newPaymentResponse;
    private MonriGooglePaymentRequestHelper monriGooglePaymentRequestHelper;
    private final int apiVersion = 2;
    private final int apiVersionMinor = 0;

    private final ActivityResultLauncher<Task<PaymentData>> paymentDataLauncher =
            registerForActivityResult(new TaskResultContracts.GetPaymentDataResult(), result -> {
                int statusCode = result.getStatus().getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.SUCCESS:
                        processRequestPaymentSuccess(result.getResult());
                        break;
                    case CommonStatusCodes.DEVELOPER_ERROR:
                        processRequestPaymentFailure(statusCode, result.getStatus().getStatusMessage());
                        break;
                    case CommonStatusCodes.CANCELED:
                        processRequestPaymentFailure(statusCode, "User cancelled");
                    default:
                        processRequestPaymentFailure(statusCode, "Unexpected non API" +
                                " exception when trying to deliver the task result to an activity!");
                        break;
                }
            });


    public static Intent createIntent(final Context context) {
        return new Intent(context, GooglePayActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_pay);

        resultTextView = findViewById(R.id.google_pay_result_textview);
        progressBar = findViewById(R.id.progress_bar);

        initializeMonriSdkObjects();
        initializeGooglePaymentsClient();
    }

    private void initializeMonriSdkObjects() {
        orderRepository = new OrderRepository(this, this);
        monri = new Monri(((ActivityResultCaller) this), MonriApiOptions.create(orderRepository.authenticityToken(), true));
    }

    private void initializeGooglePaymentsClient() {
        final Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        googlePaymentsClient = Wallet.getPaymentsClient(this, walletOptions);

        createPaymentSession();
    }

    private void createPaymentSession() {
        final Disposable createPaymentResponseObservable = orderRepository.createPayment(false)
                                                                          .subscribeOn(Schedulers.io())
                                                                          .observeOn(AndroidSchedulers.mainThread())
                                                                          .subscribe(this::processCreatePaymentSessionResponse,
                                                                                     this::processCreatePaymentSessionError);
        compositeDisposable.add(createPaymentResponseObservable);
    }

    private void processCreatePaymentSessionResponse(final NewPaymentResponse response) {
        if (!response.getStatus().equals("approved")) {
            resultTextView.setText(String.format("Create payment session rejected: %s", response.getStatus()));
        } else {
            newPaymentResponse = response;

            startGooglePayPayment();
        }
    }

    private void processCreatePaymentSessionError(final Throwable error) {
        progressBar.setVisibility(View.GONE);
        resultTextView.setText(String.format("Create payment session error: %s", error));
    }

    private void startGooglePayPayment() {
        monri.getMonriApi().startGooglePayPayment(newPaymentResponse.getId(), new ResultCallback<>() {

            @Override
            public void onSuccess(final JSONObject googlePaySessionParameters) {
                monriGooglePaymentRequestHelper = new MonriGooglePaymentRequestHelper(apiVersion, apiVersionMinor);
                monriGooglePaymentRequestHelper.setGooglePaySessionParameters(googlePaySessionParameters);

                initializeButtonAndCheckIfReadyToPayWithGoogle();
            }

            @Override
            public void onError(final Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                resultTextView.setText(String.format("Start google pay payment error: %s", throwable));
            }
        });
    }

    private void initializeButtonAndCheckIfReadyToPayWithGoogle() {
        initializeGooglePayButton();

        IsReadyToPayRequest isReadyToPayRequest = null;

        try {
            isReadyToPayRequest = monriGooglePaymentRequestHelper.getIsReadyToPayRequest();
        } catch (JSONException e) {
            resultTextView.setText("Error initializing creating isReadyToPayRequest from response");
        }

        if (isReadyToPayRequest != null) {
            googlePaymentsClient.isReadyToPay(isReadyToPayRequest)
                                .addOnSuccessListener(this::showGooglePayButton)
                                .addOnFailureListener(this::processOnReadyToPayFailure);
        } else {
            resultTextView.setText("Error: isReadyToPayRequest is null");
        }
    }

    private void initializeGooglePayButton() {
        googlePayButton = findViewById(R.id.google_pay_button);

        try {
            googlePayButton.initialize(ButtonOptions.newBuilder()
                                                    .setButtonTheme(ButtonConstants.ButtonTheme.DARK)
                                                    .setButtonType(ButtonConstants.ButtonType.PAY)
                                                    .setAllowedPaymentMethods(monriGooglePaymentRequestHelper.getAllowedPaymentMethods().toString())
                                                    .build()

            );
            googlePayButton.setOnClickListener((v) -> requestPayment());
        } catch (Exception e) {
            resultTextView.setText(String.format("Error initializing pay button: %s", e.getMessage()));
        }

        progressBar.setVisibility(View.GONE);
    }

    private void showGooglePayButton(final Boolean isReadyToPay) {
        if (isReadyToPay) {
            googlePayButton.setVisibility(View.VISIBLE);
        } else {
            googlePayButton.setVisibility(View.GONE);
            resultTextView.setText("Error: not ready to pay");
        }
    }

    private void processOnReadyToPayFailure(final Exception error) {
        resultTextView.setText(String.format("IsReadyToPay returned a failure %s", error));
    }

    private void requestPayment() {
        final JSONObject paymentRequestObject;

        try {
            paymentRequestObject = monriGooglePaymentRequestHelper.getPaymentDataRequest();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentRequestObject.toString());
        googlePaymentsClient.loadPaymentData(request)
                            .addOnCompleteListener(paymentDataLauncher::launch);
    }

    private void processRequestPaymentSuccess(final PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            googlePaymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");

            Log.d("Google Pay token", googlePaymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"));

            confirmGooglePayPayment();
        } catch (Exception e) {
            resultTextView.setText(String.format("Error parsing paymentData from requestPayment method: %s", e));
        }
    }

    private void processRequestPaymentFailure(final int statusCode, final String message) {
        resultTextView.setText(String.format("RequestPayment method failure: %s %s", statusCode, message));
    }

    private void confirmGooglePayPayment() {
        final CustomerParams customerParams = new CustomerParams()
                    .setAddress("Adresa")
                    .setFullName("Tester Testerovic")
                    .setCity("Sarajevo")
                    .setZip("71000")
                    .setPhone("+38761000111")
                    .setCountry("BA")
                    .setEmail("tester+android_sdk@monri.com");
            final GooglePayPayment payment = new GooglePayPayment(GooglePayPayment.Provider.GOOGLE_PAY, googlePaymentMethodData);

            final ConfirmPaymentParams confirmPaymentParams;
            confirmPaymentParams = ConfirmPaymentParams.create(
                    newPaymentResponse.getClientSecret(),
                    payment.toPaymentMethodParams(),
                    TransactionParams.create().set(customerParams)
            );

        monri.confirmPayment(confirmPaymentParams, this::processConfirmPaymentResult);
    }

    private void processConfirmPaymentResult(final PaymentResult result, final Throwable cause) {
        String paymentResultText;

        if (cause != null) {
            paymentResultText = String.format("Confirm payment error: %s", cause);
        } else {
           paymentResultText = String.format("Confirm payment result: %s", result.getStatus());
        }

        resultTextView.setText(paymentResultText);
    }

    @Override
    public void statusMessage(String message) {

    }
}