package com.monri.android;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jasminsuljic on 2019-09-26.
 * MonriAndroidSDK
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
                ValidationCodes.CARD_INVALID
        })
public @interface ValidationCode {
}
