package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class ConfirmPaymentResponse implements Parcelable {
    private PaymentStatus status;
    private PaymentActionRequired actionRequired;
    private PaymentResult paymentResult;
    private String id;

    public ConfirmPaymentResponse(PaymentStatus status, PaymentActionRequired actionRequired, PaymentResult paymentResult) {
        this.status = status;
        this.actionRequired = actionRequired;
        this.paymentResult = paymentResult;
    }

    public ConfirmPaymentResponse() {
    }

    public ConfirmPaymentResponse(
            final PaymentStatus status,
            final @Nullable PaymentActionRequired actionRequired,
            final @Nullable PaymentResult paymentResult,
            final String id
    ) {
        this.status = status;
        this.actionRequired = actionRequired;
        this.paymentResult = paymentResult;
        this.id = id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentActionRequired getActionRequired() {
        return actionRequired;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeParcelable(this.actionRequired, flags);
        dest.writeParcelable(this.paymentResult, flags);
        dest.writeString(this.id);
    }

    protected ConfirmPaymentResponse(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : PaymentStatus.values()[tmpStatus];
        this.actionRequired = in.readParcelable(PaymentActionRequired.class.getClassLoader());
        this.paymentResult = in.readParcelable(PaymentResult.class.getClassLoader());
        this.id = in.readString();
    }

    public static final Creator<ConfirmPaymentResponse> CREATOR = new Creator<ConfirmPaymentResponse>() {
        @Override
        public ConfirmPaymentResponse createFromParcel(Parcel source) {
            return new ConfirmPaymentResponse(source);
        }

        @Override
        public ConfirmPaymentResponse[] newArray(int size) {
            return new ConfirmPaymentResponse[size];
        }
    };

    @VisibleForTesting
    public static ConfirmPaymentResponse fromJSON(JSONObject jsonObject) throws JSONException {
        final PaymentStatus status = PaymentStatus.forValue(jsonObject.getString("status"));

        PaymentActionRequired paymentActionRequired = null;
        PaymentResult paymentResult = null;

        if (jsonObject.has("action_required")) {
            final JSONObject actionRequiredJSON = jsonObject.getJSONObject("action_required");
            final String redirectTo = actionRequiredJSON.getString("redirect_to");
            final String acsUrl = actionRequiredJSON.getString("acs_url");
            paymentActionRequired = new PaymentActionRequired(redirectTo, acsUrl);
        } else if (jsonObject.has("payment_result")) {
            final JSONObject paymentResultJSON = jsonObject.getJSONObject("payment_result");
            paymentResult = PaymentResult.fromJSON(paymentResultJSON);
        } else {
            throw new IllegalArgumentException("both action_required and payment_result are null in jsonObject");
        }

        String idFromResponse = null;

        if (jsonObject.has("client_secret")) {
            idFromResponse = jsonObject.getString("client_secret");
        }

        return new ConfirmPaymentResponse(status, paymentActionRequired, paymentResult, idFromResponse);
    }
}
