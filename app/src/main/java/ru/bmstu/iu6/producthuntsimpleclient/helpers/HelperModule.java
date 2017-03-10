/*
 * Copyright (c) 2017 Mikhail Krutyakov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.bmstu.iu6.producthuntsimpleclient.helpers;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.api.AuthInterceptor;
import rx.schedulers.Schedulers;

/**
 * Created by Михаил on 08.03.2017.
 */

@Module
public class HelperModule {
    private static final String BASE_URL = "https://api.producthunt.com/v1/";
    private static final String DEV_TOKEN =
            "591f99547f569b05ba7d8777e2e0824eea16c440292cce1f8dfb3952cc9937ff";

    public HelperModule() {}

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(new AuthInterceptor(DEV_TOKEN));
        OkHttpClient client = builder.build();

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        // TODO: use a retrofit converter
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public SharedPreferences providePreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}