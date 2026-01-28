package com.monri.android.model;

import java.util.List;

public class BaseScanDocResponse {
    protected static final String TRANSACTION_ID_KEY = "TransactionID";
    protected static final String UPLOADED_AT_KEY = "UploadedAt";
    protected static final String PRODUCT_NAME_KEY = "ProductName";
    protected static final String ERRORS_KEY = "Errors";
    protected static final String WARNINGS_KEY = "Warnings";
    protected static final String STATUS_KEY = "Status";
    protected static final String METHOD_KEY = "Method";
    protected static final String INFO_CODE_KEY = "InfoCode";
    private final String transactionID;
    private final String uploadedAt;
    private final String productName;
    private final List<String> errors;
    private final List<String> warnings;
    private final int status;
    private final String method;
    private final int infoCode;

    public BaseScanDocResponse(final String transactionID,
                               final String uploadedAt,
                               final String productName,
                               final List<String> errors,
                               final List<String> warnings,
                               final int status,
                               final String method,
                               final int infoCode) {
        this.transactionID = transactionID;
        this.uploadedAt = uploadedAt;
        this.productName = productName;
        this.errors = errors;
        this.warnings = warnings;
        this.status = status;
        this.method = method;
        this.infoCode = infoCode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public int getInfoCode() {
        return infoCode;
    }

    public String getMethod() {
        return method;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public String getProductName() {
        return productName;
    }
}
