package com.monri.android.googlepay;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class GooglePayButtonOptions implements Parcelable {
    final int buttonType;
    final int buttonTheme;
    final int cornerRadius;

    public GooglePayButtonOptions(final int buttonType, final int buttonTheme, final int cornerRadius) {
        this.buttonType = buttonType;
        this.buttonTheme = buttonTheme;
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.buttonType);
        dest.writeInt(this.buttonTheme);
        dest.writeInt(this.cornerRadius);
    }

    protected GooglePayButtonOptions(final Parcel in) {
        this.buttonType = in.readInt();
        this.buttonTheme = in.readInt();
        this.cornerRadius = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public int getButtonTheme() {
        return buttonTheme;
    }

    public int getButtonType() {
        return buttonType;
    }

    public static final Parcelable.Creator<GooglePayButtonOptions> CREATOR = new Parcelable.Creator<>() {
        @Override
        public GooglePayButtonOptions createFromParcel(final Parcel source) {
            return new GooglePayButtonOptions(source);
        }

        @Override
        public GooglePayButtonOptions[] newArray(final int size) {
            return new GooglePayButtonOptions[size];
        }
    };
}
