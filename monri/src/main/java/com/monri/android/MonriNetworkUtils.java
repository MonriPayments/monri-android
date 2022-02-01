package com.monri.android;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Map;

/**
 * Utility class for static functions useful for networking and data transfer. You probably will
 * not need to call functions from this class in your code.
 */
public class MonriNetworkUtils {

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
