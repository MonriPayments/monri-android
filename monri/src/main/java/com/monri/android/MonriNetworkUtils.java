package com.monri.android;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.monri.android.model.Card;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Utility class for static functions useful for networking and data transfer. You probably will
 * not need to call functions from this class in your code.
 */
public class MonriNetworkUtils {

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("ConstantConditions")
    @NonNull
    static Map<String, Object> hashMapFromCard(
            Card card, TokenRequest tokenRequest, String authenticityToken) {

        Map<String, Object> params = new HashMap<>();

        params.put("authenticity_token", authenticityToken);
        params.put("temp_card_id", tokenRequest.getToken());
        params.put("timestamp", MonriTextUtils.nullIfBlank(tokenRequest.getTimestamp()));
        params.put("pan", MonriTextUtils.nullIfBlank(card.getNumber()));
        params.put("expiration_date", String.format("%d%02d", card.getExpYear() - 2000, card.getExpMonth()));
        params.put("cvv", MonriTextUtils.nullIfBlank(card.getCVC()));
        params.put("digest", tokenRequest.getDigest());
        params.put("token", tokenRequest.getToken());
        params.put("tokenize_pan", false);

        // Remove all null values; they cause validation errors
        removeNullAndEmptyParams(params);
        return params;
    }

    /**
     * Remove null values from a map. This helps with JSON conversion and validation.
     *
     * @param mapToEdit a {@link Map} from which to remove the keys that have {@code null} values
     */
    @SuppressWarnings("unchecked")
    public static void removeNullAndEmptyParams(@NonNull Map<String, Object> mapToEdit) {
        // Remove all null values; they cause validation errors
        for (String key : new HashSet<>(mapToEdit.keySet())) {
            if (mapToEdit.get(key) == null) {
                mapToEdit.remove(key);
            }

            if (mapToEdit.get(key) instanceof CharSequence) {
                CharSequence sequence = (CharSequence) mapToEdit.get(key);
                if (TextUtils.isEmpty(sequence)) {
                    mapToEdit.remove(key);
                }
            }

            if (mapToEdit.get(key) instanceof Map) {
                Map<String, Object> stringObjectMap = (Map<String, Object>) mapToEdit.get(key);
                removeNullAndEmptyParams(stringObjectMap);
            }
        }
    }

}
