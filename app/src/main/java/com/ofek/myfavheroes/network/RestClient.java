/*
 * Created by Ofek Pintok on 5/30/19 8:25 AM
 */

package com.ofek.myfavheroes.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RestClient {

    private static HeroService heroService;

    // Build OkHttpClient
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .build();

    public static HeroService getHeroService() {
        if (heroService == null) {
            // Build a Retrofit client, then create hero service
            Retrofit retrofit = new Retrofit.Builder().baseUrl(HeroService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
            heroService = retrofit.create(HeroService.class);
        }
        return heroService;
    }
}
