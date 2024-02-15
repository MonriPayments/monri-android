package com.monri.android.activity;

import com.monri.android.activity.fake.FakeMonriApi;
import com.monri.android.activity.fake.FakeScheduledExecutorService;
import com.monri.android.activity.fake.FakeUiDelegate;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.DirectPayment;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.TransactionParams;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfirmDirectPaymentFlowTest {

    private static final String PAYMENT_ID = "paymentId";
    private static final String AUTHENTICITY_TOKEN = "authenticityToken";

    @Test
    public void createShouldThrowExceptionWhenInputIsNullParameter() {
        // Given
        final FakeScheduledExecutorService backgroundThreadExecutor = new FakeScheduledExecutorService();
        final FakeUiDelegate uiDelegate = new FakeUiDelegate();
        final FakeMonriApi monriApi = new FakeMonriApi(null);
        final ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(PAYMENT_ID, new DirectPayment().toPaymentMethodParams(), new TransactionParams());
        final MonriApiOptions monriApiOptions = new MonriApiOptions(AUTHENTICITY_TOKEN, true);

        // When
        final ThrowingRunnable nullBackgroundThreadExecutor = () -> ConfirmDirectPaymentFlow.create(null, uiDelegate, monriApi, confirmPaymentParams, monriApiOptions);
        final ThrowingRunnable nullUiDelegate = () -> ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, null, monriApi, confirmPaymentParams, monriApiOptions);
        final ThrowingRunnable nullMonriApi = () -> ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, uiDelegate, null, confirmPaymentParams, monriApiOptions);
        final ThrowingRunnable nullConfirmPaymentParams = () -> ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, uiDelegate, monriApi, null, monriApiOptions);
        final ThrowingRunnable nullMonriApiOptions = () -> ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, uiDelegate, monriApi, confirmPaymentParams, null);

        // Then
        Assert.assertThrows(NullPointerException.class, nullBackgroundThreadExecutor);
        Assert.assertThrows(NullPointerException.class, nullUiDelegate);
        Assert.assertThrows(NullPointerException.class, nullMonriApi);
        Assert.assertThrows(NullPointerException.class, nullConfirmPaymentParams);
        Assert.assertThrows(NullPointerException.class, nullMonriApiOptions);
    }

    @Test
    public void createShouldReturnInstanceWithValidInputParameters() {
        // Given
        final FakeScheduledExecutorService backgroundThreadExecutor = new FakeScheduledExecutorService();
        final FakeUiDelegate uiDelegate = new FakeUiDelegate();
        final FakeMonriApi monriApi = new FakeMonriApi(null);
        final ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(PAYMENT_ID, new DirectPayment().toPaymentMethodParams(), new TransactionParams());
        final MonriApiOptions monriApiOptions = new MonriApiOptions(AUTHENTICITY_TOKEN, true);

        ConfirmDirectPaymentFlow confirmDirectPaymentFlow;

        // When
        confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, uiDelegate, monriApi, confirmPaymentParams, monriApiOptions);

        // Then
        Assert.assertNotNull(confirmDirectPaymentFlow);
    }

    @Test
    public void executeShouldLoadCorrectUrlAndRenderUi() {
        // Given
        final FakeUiDelegate uiDelegate = new FakeUiDelegate();
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(
                new FakeScheduledExecutorService(),
                uiDelegate,
                new FakeMonriApi(null),
                ConfirmPaymentParams.create(PAYMENT_ID, new DirectPayment().toPaymentMethodParams(), new TransactionParams()),
                new MonriApiOptions(AUTHENTICITY_TOKEN, true)
        );

        final String expectedUrl = String.format("https://ipgtest.monri.com/v2/direct-payment/pay-cek-hr/%s/redirect-to-payment-url", PAYMENT_ID);

        // When
        confirmDirectPaymentFlow.execute();

        // Then
        Assert.assertTrue(uiDelegate.isWebViewVisible());
        Assert.assertTrue(uiDelegate.isLoadingVisible());
        Assert.assertEquals(expectedUrl, uiDelegate.getCurrentUrl());
    }

    @Test
    public void executeShouldKeepCheckingForStatusWhenHandlingPaymentMethodRequiredStatus() {
        // Given
        final int expectedStatusChecksCount = 5;

        final PaymentResult expectedPaymentResult = new PaymentResult("transaction_status");
        final PaymentStatusResponse paymentMethodRequiredResponse = new PaymentStatusResponse(PaymentStatus.PAYMENT_METHOD_REQUIRED, PaymentStatus.PAYMENT_METHOD_REQUIRED.getStatus(), expectedPaymentResult);
        final PaymentStatusResponse approvedResponse = new PaymentStatusResponse(PaymentStatus.APPROVED, PaymentStatus.APPROVED.getStatus(), expectedPaymentResult);

        final AtomicInteger statusCheckedCount = new AtomicInteger();
        final FakeMonriApi.PaymentStatusHandler paymentStatusHandler = (params, callback) -> {
            if (statusCheckedCount.incrementAndGet() < expectedStatusChecksCount) {
                callback.onSuccess(paymentMethodRequiredResponse);
            } else {
                callback.onSuccess(approvedResponse);
            }
        };

        final FakeUiDelegate uiDelegate = new FakeUiDelegate();
        final FakeMonriApi fakeMonriApi = new FakeMonriApi(paymentStatusHandler);
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(
                new FakeScheduledExecutorService(),
                uiDelegate,
                fakeMonriApi,
                ConfirmPaymentParams.create(PAYMENT_ID, new DirectPayment().toPaymentMethodParams(), new TransactionParams()),
                new MonriApiOptions(AUTHENTICITY_TOKEN, true)
        );

        // When
        confirmDirectPaymentFlow.execute();

        // Then
        Assert.assertEquals(expectedStatusChecksCount, fakeMonriApi.getStatusCheckInvokedCount());
        Assert.assertNotNull(uiDelegate.getPaymentResultToHandle());
    }

    @Test
    public void executeShouldReturnExpectedPaymentResultAndRenderUiWhenPaymentStatusIsApproved() {
        // Given
        final PaymentResult expectedPaymentResult = new PaymentResult("transaction_status");

        final FakeMonriApi.PaymentStatusHandler paymentStatusHandler = (params, callback) -> callback.onSuccess(new PaymentStatusResponse(PaymentStatus.APPROVED, PaymentStatus.APPROVED.getStatus(), expectedPaymentResult));
        final FakeUiDelegate uiDelegate = new FakeUiDelegate();
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(
                new FakeScheduledExecutorService(),
                uiDelegate,
                new FakeMonriApi(paymentStatusHandler),
                ConfirmPaymentParams.create(PAYMENT_ID, new DirectPayment().toPaymentMethodParams(), new TransactionParams()),
                new MonriApiOptions(AUTHENTICITY_TOKEN, true)
        );

        // When
        confirmDirectPaymentFlow.execute();

        // Then
        Assert.assertEquals(expectedPaymentResult, uiDelegate.getPaymentResultToHandle());
        Assert.assertFalse(uiDelegate.isWebViewVisible());
        Assert.assertFalse(uiDelegate.isLoadingVisible());
    }

    @Test
    public void executeShouldReturnPaymentResultWhenGetPaymentStatusReturnsError() {
        // Given
        final FakeMonriApi.PaymentStatusHandler paymentStatusHandler = (params, callback) -> callback.onError(new IOException("Network exception!"));
        final FakeUiDelegate uiDelegate = new FakeUiDelegate();
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(
                new FakeScheduledExecutorService(),
                uiDelegate,
                new FakeMonriApi(paymentStatusHandler),
                ConfirmPaymentParams.create(PAYMENT_ID, new DirectPayment().toPaymentMethodParams(), new TransactionParams()),
                new MonriApiOptions(AUTHENTICITY_TOKEN, true)
        );

        // When
        confirmDirectPaymentFlow.execute();

        // Then
        final PaymentResult actualPaymentResult = uiDelegate.getPaymentResultToHandle();
        Assert.assertNotNull(actualPaymentResult);
        Assert.assertNotNull(actualPaymentResult.getErrors());
        Assert.assertFalse(actualPaymentResult.getErrors().isEmpty());
    }
}
