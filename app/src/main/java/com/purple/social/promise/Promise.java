package com.purple.social.promise;


import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.purple.social.api.models.ApiResponse;
//import com.zigo.social.helper.ErrorHelper;
import com.purple.social.net.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Promise<T> {

    public interface MyConsumer<V> {
        void accept(V value);
    }

    private Call<ApiResponse<T>> call;
    private MyConsumer<ApiResponse<T>> success;
    private MyConsumer<ApiResponse<T>> both;
    private MyConsumer<ApiResponse<T>> error;
    private MyConsumer<Throwable> fail;

    public Promise() {
        success = x -> {
        };
        both = x -> {
        };
        error = x -> {
        };
        fail = x -> {
        };
    }

    public Promise<T> promise(Call<ApiResponse<T>> call) {
        this.call = call;
        return this;
    }

    public void call(Context context) {
        if (call != null)
            call.enqueue(new Callback<ApiResponse<T>>() {
                @Override
                public void onResponse(@NotNull Call<ApiResponse<T>> call, @NotNull Response<ApiResponse<T>> response) {
                    ApiResponse<T> apiResponse = response.body();

                    if (apiResponse != null) {
                        success.accept(apiResponse);
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String s = response.errorBody().string();

                                Type type = new TypeToken<ApiResponse<T>>() {
                                }.getType();

                                ApiResponse<T> errorResponse = Utility.getInstance().gson.fromJson(s, type);
//                                ErrorHelper.getInstance().detectMessage(context, errorResponse.getMessage(), errorResponse.getStatusCode());
                                error.accept(errorResponse);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    both.accept(null);
                }

                @Override
                public void onFailure(@NotNull Call<ApiResponse<T>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                    fail.accept(t);
                }
            });
    }

    public Promise<T> onSuccess(MyConsumer<ApiResponse<T>> success) {
        this.success = success;
        return this;
    }

    public Promise<T> onBoth(MyConsumer<ApiResponse<T>> both) {
        this.both = both;
        return this;
    }

    public Promise<T> onError(MyConsumer<ApiResponse<T>> error) {
        this.error = error;
        return this;
    }

    public Promise<T> onFail(MyConsumer<Throwable> fail) {
        this.fail = fail;
        return this;
    }
}
