package com.monri.android.model;

import android.os.Parcelable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
@JsonDeserialize(using = SavedPaymentMethodDeserializer.class)
public abstract class SavedPaymentMethod<T extends Parcelable> implements Parcelable {
    public abstract String getType();

    public abstract T getData();
}
