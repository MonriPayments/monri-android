package com.monri.android.example;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.monri.android.model.ScanDocExtractResponse;

public class ExtractedData implements Parcelable {
    private final String analysisTime;
    private final String holderName;
    private final String luhnCheck;
    private final String cardNumber;
    private final String expiryDate;
    private final String extractedTexts;
    private final String issuedDate;
    private final String base64ImageData;
    private final String iban;

    protected ExtractedData(final Parcel in) {
        analysisTime = in.readString();
        holderName = in.readString();
        luhnCheck = in.readString();
        cardNumber = in.readString();
        expiryDate = in.readString();
        extractedTexts = in.readString();
        issuedDate = in.readString();
        base64ImageData = in.readString();
        iban = in.readString();
    }

    public ExtractedData(
            final String analysisTime,
            final String holderName,
            final String luhnCheck,
            final String cardNumber,
            final String expiryDate,
            final String extractedTexts,
            final String issuedDate,
            final String base64ImageData,
            final String iban) {
        this.analysisTime = analysisTime;
        this.holderName = holderName;
        this.luhnCheck = luhnCheck;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.extractedTexts = extractedTexts;
        this.issuedDate = issuedDate;
        this.base64ImageData = base64ImageData;
        this.iban = iban;
    }

    public static ExtractedData fromExtractionResponse(final ScanDocExtractResponse extractResponse) {
        return new ExtractedData(
                extractResponse.getAnalysisTime(),
                extractResponse.getCardData().getHoldersName(),
                extractResponse.getCardData().getLuhnCheck(),
                extractResponse.getCardData().getCardNumber(),
                extractResponse.getCardData().getExpiryDate(),
                extractResponse.getCardData().getExtractedTexts(),
                extractResponse.getCardData().getIssuedDate(),
                extractResponse.getBase64CreditCardImage(),
                extractResponse.getCardData().getIBAN()
        );
    }

    public static final Creator<ExtractedData> CREATOR = new Creator<>() {
        @Override
        public ExtractedData createFromParcel(Parcel in) {
            return new ExtractedData(in);
        }

        @Override
        public ExtractedData[] newArray(int size) {
            return new ExtractedData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(analysisTime);
        dest.writeString(holderName);
        dest.writeString(luhnCheck);
        dest.writeString(cardNumber);
        dest.writeString(expiryDate);
        dest.writeString(extractedTexts);
        dest.writeString(issuedDate);
        dest.writeString(base64ImageData);
        dest.writeString(iban);
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getExtractedTexts() {
        return extractedTexts;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public String getBase64ImageData() {
        return base64ImageData;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getLuhnCheck() {
        return luhnCheck;
    }

    public String getAnalysisTime() {
        return analysisTime;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getIban() {
        return iban;
    }
}
