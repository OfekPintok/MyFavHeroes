/*
 * Created by Ofek Pintok on 5/30/19 8:25 AM
 */

package com.ofek.myfavheroes.network;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ofek.myfavheroes.model.HeroResponse;
import com.ofek.myfavheroes.model.HeroModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkComponent {

    public void getHeroes(final OnReceivedDataListener callListener) {

        Call<List<HeroResponse>> call = RestClient.getHeroService().getHeroes();
        call.enqueue(new Callback<List<HeroResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<HeroResponse>> call,@NonNull Response<List<HeroResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<HeroModel> heroList = new ArrayList<>();

                    for (HeroResponse heroResponse : response.body()) {

                        HeroModel heroModel = new HeroModel(heroResponse.getTitle(),
                                heroResponse.getAbilities(),
                                heroResponse.getImage());

                        heroList.add(heroModel);
                    }

                    // Pass the results back to the repository
                    callListener.onSuccess(heroList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<HeroResponse>> call,@NonNull Throwable t) {
                Log.d("NetworkComponent", "Data couldn't be loaded: " + t);
                callListener.onFailure(new NetworkErrorException(t));
            }
        });
    }

    public interface OnReceivedDataListener {

        void onSuccess(List<HeroModel> heroList);

        void onFailure(NetworkErrorException e);
    }

}
