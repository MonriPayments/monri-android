package com.monri.android.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BrowserInfo implements Parcelable {

    private static final int COLOR_DEPTH = 24;
    private static final int MILLIS_PER_MINUTE = 60000;
    private static final String HTTP_ACCEPT = "*/*";
    private static final String SYSTEM_USER_AGENT_KEY = "http.agent";
    private static final String SCREEN_WIDTH_KEY = "screen_width";
    private static final String SCREEN_HEIGHT_KEY = "screen_height";
    private static final String COLOR_DEPTH_KEY = "color_depth";
    private static final String USER_AGENT_KEY = "user_agent";
    private static final String TIME_ZONE_OFFSET_KEY = "time_zone_offset";
    private static final String LANGUAGE_KEY = "language";
    private static final String JAVA_ENABLED_KEY = "java_enabled";
    private static final String HTTP_ACCEPT_KEY = "http_accept";
    private static final String HTTP_USER_AGENT_KEY = "http_user_agent";
    private static final String HTTP_ACCEPT_LANGUAGE_KEY = "http_accept_language";

    private int screenWidth;
    private int screenHeight;
    private int colorDepth;
    private String userAgent;
    private int timeZoneOffset;
    private String language;
    private boolean javaEnabled;
    private String httpAccept;
    private String httpUserAgent;
    private String httpAcceptLanguage;

    public BrowserInfo(final int screenWidth,
                       final int screenHeight,
                       final int colorDepth,
                       final String userAgent,
                       final int timeZoneOffset,
                       final String language,
                       final boolean javaEnabled,
                       final String httpAccept,
                       final String httpUserAgent,
                       final String httpAcceptLanguage) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.colorDepth = colorDepth;
        this.userAgent = userAgent;
        this.timeZoneOffset = timeZoneOffset;
        this.language = language;
        this.javaEnabled = javaEnabled;
        this.httpAccept = httpAccept;
        this.httpUserAgent = httpUserAgent;
        this.httpAcceptLanguage = httpAcceptLanguage;
    }

    @NonNull
    public static BrowserInfo create(@NonNull final Context context) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final String userAgent = resolveUserAgent(context);
        final String language = Locale.getDefault().getLanguage();

        return new BrowserInfo(
                displayMetrics.widthPixels,
                displayMetrics.heightPixels,
                COLOR_DEPTH,
                userAgent,
                resolveTimeZoneOffset(),
                language,
                false,
                HTTP_ACCEPT,
                userAgent,
                language
        );
    }

    private static String resolveUserAgent(@NonNull final Context context) {
        try {
            return WebSettings.getDefaultUserAgent(context);
        } catch (final Exception e) {
            final String systemUserAgent = System.getProperty(SYSTEM_USER_AGENT_KEY);
            return systemUserAgent != null ? systemUserAgent : "";
        }
    }

    private static int resolveTimeZoneOffset() {
        final int offsetMillis = TimeZone.getDefault().getOffset(new Date().getTime());
        return -offsetMillis / MILLIS_PER_MINUTE;
    }

    @NonNull
    public JSONObject toJSON() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(SCREEN_WIDTH_KEY, screenWidth);
        json.put(SCREEN_HEIGHT_KEY, screenHeight);
        json.put(COLOR_DEPTH_KEY, colorDepth);
        json.put(USER_AGENT_KEY, userAgent);
        json.put(TIME_ZONE_OFFSET_KEY, timeZoneOffset);
        json.put(LANGUAGE_KEY, language);
        json.put(JAVA_ENABLED_KEY, javaEnabled);
        json.put(HTTP_ACCEPT_KEY, httpAccept);
        json.put(HTTP_USER_AGENT_KEY, httpUserAgent);
        json.put(HTTP_ACCEPT_LANGUAGE_KEY, httpAcceptLanguage);
        return json;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public BrowserInfo setScreenWidth(final int screenWidth) {
        this.screenWidth = screenWidth;
        return this;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public BrowserInfo setScreenHeight(final int screenHeight) {
        this.screenHeight = screenHeight;
        return this;
    }

    public int getColorDepth() {
        return colorDepth;
    }

    public BrowserInfo setColorDepth(final int colorDepth) {
        this.colorDepth = colorDepth;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public BrowserInfo setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public BrowserInfo setTimeZoneOffset(final int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public BrowserInfo setLanguage(final String language) {
        this.language = language;
        return this;
    }

    public boolean isJavaEnabled() {
        return javaEnabled;
    }

    public BrowserInfo setJavaEnabled(final boolean javaEnabled) {
        this.javaEnabled = javaEnabled;
        return this;
    }

    public String getHttpAccept() {
        return httpAccept;
    }

    public BrowserInfo setHttpAccept(final String httpAccept) {
        this.httpAccept = httpAccept;
        return this;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public BrowserInfo setHttpUserAgent(final String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
        return this;
    }

    public String getHttpAcceptLanguage() {
        return httpAcceptLanguage;
    }

    public BrowserInfo setHttpAcceptLanguage(final String httpAcceptLanguage) {
        this.httpAcceptLanguage = httpAcceptLanguage;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(screenWidth);
        dest.writeInt(screenHeight);
        dest.writeInt(colorDepth);
        dest.writeString(userAgent);
        dest.writeInt(timeZoneOffset);
        dest.writeString(language);
        dest.writeByte((byte) (javaEnabled ? 1 : 0));
        dest.writeString(httpAccept);
        dest.writeString(httpUserAgent);
        dest.writeString(httpAcceptLanguage);
    }

    protected BrowserInfo(final Parcel in) {
        this.screenWidth = in.readInt();
        this.screenHeight = in.readInt();
        this.colorDepth = in.readInt();
        this.userAgent = in.readString();
        this.timeZoneOffset = in.readInt();
        this.language = in.readString();
        this.javaEnabled = in.readByte() != 0;
        this.httpAccept = in.readString();
        this.httpUserAgent = in.readString();
        this.httpAcceptLanguage = in.readString();
    }

    public static final Creator<BrowserInfo> CREATOR = new Creator<BrowserInfo>() {
        @Override
        public BrowserInfo createFromParcel(final Parcel source) {
            return new BrowserInfo(source);
        }

        @Override
        public BrowserInfo[] newArray(final int size) {
            return new BrowserInfo[size];
        }
    };
}
