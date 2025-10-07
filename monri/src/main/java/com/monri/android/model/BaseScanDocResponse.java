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
    public final String transactionID;
    public final String uploadedAt;
    public final String productName;
    public final List<String> errors;
    public final List<String> warnings;
    public final int status;
    public final String method;
    public final int infoCode;

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
}
