package com.monri.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class PaymentStatusResponse implements Parcelable {

    private final PaymentStatus paymentStatus;
    private final String status;
    @Nullable
    private final PaymentResult paymentResult;

    public PaymentStatusResponse(final PaymentStatus paymentStatus, final String status, @Nullable final PaymentResult paymentResult) {
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.paymentResult = paymentResult;
    }

    @Nullable
    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    @Nullable
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.paymentStatus == null ? -1 : this.paymentStatus.ordinal());
        dest.writeString(this.status);
        dest.writeParcelable(this.paymentResult, flags);
    }

    protected PaymentStatusResponse(Parcel in) {
        int tmpPaymentStatus = in.readInt();
        this.paymentStatus = tmpPaymentStatus == -1 ? null : PaymentStatus.values()[tmpPaymentStatus];
        this.status = in.readString();
        this.paymentResult = in.readParcelable(PaymentResult.class.getClassLoader());
    }

    public static final Creator<PaymentStatusResponse> CREATOR = new Creator<>() {
        @Override
        public PaymentStatusResponse createFromParcel(Parcel source) {
            return new PaymentStatusResponse(source);
        }

        @Override
        public PaymentStatusResponse[] newArray(int size) {
            return new PaymentStatusResponse[size];
        }
    };

    public static PaymentStatusResponse fromJSON(final JSONObject paymentStatusResponseJSON) throws JSONException {
        final String status = paymentStatusResponseJSON.getString("status");
        final PaymentStatus paymentStatus = PaymentStatus.forValue(paymentStatusResponseJSON.getString("payment_status"));

        final JSONObject paymentResultJSON = paymentStatusResponseJSON.getJSONObject("payment_result");
        final PaymentResult paymentResult = PaymentResult.fromJSON(paymentResultJSON);

        return new PaymentStatusResponse(
                paymentStatus,
                status,
                paymentResult
        );
    }
}
