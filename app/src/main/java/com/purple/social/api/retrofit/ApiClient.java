package com.purple.social.api.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.purple.social.R;
import com.purple.social.net.Utility;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zigo on 09.05.2020.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofit == null) {
            String base_Url = Utility.getInstance().getString(context, R.string.base_url);
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_Url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(new OkHttpClient())
                    .build();
            return retrofit;
        }
        return retrofit;
    }
}
