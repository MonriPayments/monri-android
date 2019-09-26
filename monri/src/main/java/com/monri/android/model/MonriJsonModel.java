package com.monri.android.model;


import androidx.annotation.NonNull;

import org.json.JSONObject;

/**
 * Represents a JSON model used in the Monri Api.
 */
public abstract class MonriJsonModel {

    @NonNull
    public abstract JSONObject toJson();

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MonriJsonModel)) {
            return false;
        }

        MonriJsonModel otherModel = (MonriJsonModel) obj;
        return this.toString().equals(otherModel.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }


}
