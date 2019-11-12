package com.monri.android.example;


import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-10-30.
 * MonriAndroid
 */
class OrderResponse {

    static final String STATUS_ACTION_REQUIRED = "action_required";
    static final String STATUS_DECLINED = "declined";
    static final String STATUS_APPROVED = "approved";

    @JsonProperty("status") private
    String status;

    @Nullable
    @JsonProperty("action")
    private
    Action action;

    @Nullable
    @JsonProperty("transaction")
    private
    Transaction transaction;

    public OrderResponse() {
    }

    @Override
    public String toString() {
        return "OrderResponse{" + "status='" + status + '\'' +
                ", action=" + action +
                ", transaction=" + transaction +
                '}';
    }

    public String getStatus() {
        return status;
    }

    @Nullable
    public Action getAction() {
        return action;
    }

    @Nullable
    public Transaction getTransaction() {
        return transaction;
    }

    static class Action {
        @JsonProperty("redirect_to")
        String redirectTo;

        public Action() {
        }

        public Action(String redirectTo) {
            this.redirectTo = redirectTo;
        }

        public String getRedirectTo() {
            return redirectTo;
        }

        @Override
        public String toString() {
            return "Action{" + "redirectTo='" + redirectTo + '\'' +
                    '}';
        }
    }

    static class Transaction {

        @JsonProperty("status")
        String status;

        @JsonProperty("order_number")
        String orderNumber;

        @JsonProperty("pan_token")
        String panToken;

        public Transaction() {
        }

        public Transaction(String status, String orderNumber) {
            this.status = status;
            this.orderNumber = orderNumber;
        }

        public String getStatus() {
            return status;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        @Override
        public String toString() {
            return "Transaction{" + "status='" + status + '\'' +
                    ", orderNumber='" + orderNumber + '\'' +
                    '}';
        }

        public String getPanToken() {
            return panToken;
        }
    }
}
