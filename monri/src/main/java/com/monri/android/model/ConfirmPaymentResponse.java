package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class ConfirmPaymentResponse implements Parcelable {
    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("action_required")
    private PaymentActionRequired actionRequired;

    @JsonProperty("payment_result")
    private PaymentResult paymentResult;

    @JsonProperty("id")
    private String id;

    public ConfirmPaymentResponse(PaymentStatus status, PaymentActionRequired actionRequired, PaymentResult paymentResult) {
        this.status = status;
        this.actionRequired = actionRequired;
        this.paymentResult = paymentResult;
    }

    public ConfirmPaymentResponse() {
    }

    public ConfirmPaymentResponse(final PaymentStatus status, final PaymentActionRequired actionRequired, final PaymentResult paymentResult, final String id) {
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
}
