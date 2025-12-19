package com.monri.android.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.monri.android.model.ScanDocExtractResponse;

public class ScanDocExtractedDataFragment extends Fragment {
    private static final String BUNDLE_EXTRACTED_DATA_KEY = "extractedData";
    private LabeledTextView holderName;
    private LabeledTextView iban;
    private LabeledTextView issuedDate;
    private LabeledTextView cardNumber;
    private LabeledTextView expiryDate;
    private LabeledTextView luhnCheck;
    private LabeledTextView extractedTexts;
    private LabeledTextView analysisTime;
    private ImageView imageView;
    private ExtractedData extractedData;

    public static ScanDocExtractedDataFragment newInstance(final ScanDocExtractResponse extractResponse) {
        final Bundle bundle = new Bundle();

        bundle.putParcelable(BUNDLE_EXTRACTED_DATA_KEY, ExtractedData.fromExtractionResponse(extractResponse));

        final ScanDocExtractedDataFragment newInstance =  new ScanDocExtractedDataFragment();
        newInstance.setArguments(bundle);

        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scandoc_extracted_data, container, false);

        getArgumentsFromBundle();
        initializeViews(view);

        return view;
    }

    private void getArgumentsFromBundle() {
        extractedData = getArguments().getParcelable(BUNDLE_EXTRACTED_DATA_KEY);
    }

    private void initializeViews(final View rootView) {
        holderName = rootView.findViewById(R.id.holder_name);
        iban = rootView.findViewById(R.id.iban);
        issuedDate = rootView.findViewById(R.id.issued_date);
        cardNumber = rootView.findViewById(R.id.card_number);
        expiryDate = rootView.findViewById(R.id.expiry_date);
        luhnCheck = rootView.findViewById(R.id.luhn_check);
        extractedTexts = rootView.findViewById(R.id.extracted_texts);
        imageView = rootView.findViewById(R.id.image_data);
        analysisTime = rootView.findViewById(R.id.analysis_time);

        analysisTime.setLabel(getString(R.string.scandoc_extracted_data_fragment_analysis_time_label));
        analysisTime.setValue(extractedData.getAnalysisTime());

        holderName.setLabel(getString(R.string.scandoc_extracted_data_fragment_holder_name_label));
        holderName.setValue(extractedData.getHolderName());

        iban.setLabel(getString(R.string.scandoc_extracted_data_fragment_iban_label));
        iban.setValue(extractedData.getIban());

        issuedDate.setLabel(getString(R.string.scandoc_extracted_data_fragment_issued_date_label));
        issuedDate.setValue(extractedData.getIssuedDate());

        cardNumber.setLabel(getString(R.string.scandoc_extracted_data_fragment_card_number_label));
        cardNumber.setValue(extractedData.getCardNumber());

        expiryDate.setLabel(getString(R.string.scandoc_extracted_data_fragment_expiry_date_label));
        expiryDate.setValue(extractedData.getExpiryDate());

        luhnCheck.setLabel(getString(R.string.scandoc_extracted_data_fragment_luhn_check_label));
        luhnCheck.setValue(extractedData.getLuhnCheck());

        extractedTexts.setLabel(getString(R.string.scandoc_extracted_data_fragment_extracted_texts_label));
        extractedTexts.setValue(extractedData.getExtractedTexts());

        imageView.setImageBitmap(ImageProcessingUtil.base64ToImage(extractedData.getBase64ImageData()));
    }
}
