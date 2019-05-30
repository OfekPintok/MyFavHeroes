/*
 * Created by Ofek Pintok on 5/30/19 8:25 AM
 */

package com.ofek.myfavheroes.network;

import com.ofek.myfavheroes.model.HeroResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HeroService {

    String BASE_URL = "https://heroapps.co.il/employee-tests/android/";

    String EXTENSION = "androidexam.json";

    @GET(EXTENSION)
    Call<List<HeroResponse>> getHeroes();

}
