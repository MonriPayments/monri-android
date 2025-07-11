package com.monri.android.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class WebPayModule {

    private final String baseUrl;

    public WebPayModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private ObjectMapper objectMapper(SimpleModule simpleModule) {

        return new ObjectMapper()
                .registerModule(simpleModule)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    }

    private Retrofit provideWebPayRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory(objectMapper(new SimpleModule())))
                .addCallAdapterFactory(callAdapterFactory())
                .client(provideOkHttpClient())
                .build();
    }

    WebPayApi webPayApi() {
        return provideWebPayRetrofit().create(WebPayApi.class);
    }

    private CallAdapter.Factory callAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    private Converter.Factory converterFactory(ObjectMapper objectMapper) {
        return JacksonConverterFactory.create(objectMapper);
    }


    private OkHttpClient provideOkHttpClient() {
        final List<Interceptor> requestInterceptors = getRequestInterceptors();

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        for (Interceptor requestInterceptor : requestInterceptors) {
            builder.addInterceptor(requestInterceptor);
        }

        final List<Interceptor> responseInterceptors = getResponseInterceptors();

        for (Interceptor responseInterceptor : responseInterceptors) {
            builder.addNetworkInterceptor(responseInterceptor);
        }

        return builder.build();
    }


    private List<Interceptor> getResponseInterceptors() {
        return Collections.emptyList();
    }

    List<Interceptor> getRequestInterceptors() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return Collections.singletonList(
                httpLoggingInterceptor
        );
    }
}
