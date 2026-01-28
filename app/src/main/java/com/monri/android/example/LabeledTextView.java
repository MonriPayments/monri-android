package com.monri.android.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class LabeledTextView extends LinearLayout {
    private TextView label;
    private TextView value;

    public LabeledTextView(final Context context) {
        super(context);
        init(context);
    }

    public LabeledTextView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public LabeledTextView(final Context context, final @Nullable AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LabeledTextView(final Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_labeled_textview, this, true);

        label = findViewById(R.id.label);
        value = findViewById(R.id.value);
    }

    public TextView getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label.setText(label);
    }

    public TextView getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value.setText(value);
    }
}
