
/*
 * Created by Ofek Pintok on 5/30/19 8:24 AM
 */

package com.ofek.myfavheroes.viewmodel;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ofek.myfavheroes.model.HeroModel;
import com.ofek.myfavheroes.network.NetworkComponent;
import com.ofek.myfavheroes.repository.HeroRepository;

import java.util.ArrayList;
import java.util.List;

public class HeroViewModel extends ViewModel {

    private MutableLiveData<ArrayList<HeroModel>> mHeroList = new MutableLiveData<>();

    public HeroViewModel() {
    }

    public MutableLiveData<ArrayList<HeroModel>> getObservableHeroList() {
        return mHeroList;
    }

    public void loadData(final OnLoadedDataListener onLoadedDataListener) {
        HeroRepository.getInstance().getHeroList(new NetworkComponent.OnReceivedDataListener() {
            @Override
            public void onSuccess(List<HeroModel> heroList) {
                ArrayList<HeroModel> heroArrayList = new ArrayList<>(heroList);
                setHeroList(heroArrayList);
                Log.i("HeroViewModel", "Hero list was successfully loaded.");
                onLoadedDataListener.OnFinished();
            }

            @Override
            public void onFailure(NetworkErrorException e) {
                onLoadedDataListener.onFailed(e);
            }
        });
    }

    public void setHeroList(ArrayList<HeroModel> heroList) {
        mHeroList.setValue(heroList);
    }


    public interface OnLoadedDataListener {

        void OnFinished();

        void onFailed(Exception e);
    }
}
