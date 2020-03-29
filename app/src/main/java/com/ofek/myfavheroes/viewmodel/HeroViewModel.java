
/*
 * Created by Ofek Pintok on 5/30/19 8:24 AM
 */

package com.ofek.myfavheroes.viewmodel;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ofek.myfavheroes.R;
import com.ofek.myfavheroes.model.HeroModel;
import com.ofek.myfavheroes.network.NetworkComponent;
import com.ofek.myfavheroes.repository.HeroRepository;
import com.ofek.myfavheroes.storage.InternalStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeroViewModel extends AndroidViewModel {

    private static final String STORAGE_KEY = "Internal_Storage_Key";

    private MutableLiveData<ArrayList<HeroModel>> mHeroList = new MutableLiveData<>();

    public HeroViewModel(@NonNull Application application) {
        super(application);
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

                saveChanges();
            }

            @Override
            public void onFailure(NetworkErrorException e) {
                onLoadedDataListener.onFailed(e);
            }
        });
    }

    public ArrayList<HeroModel> getCachedData() {
        try {
            return (ArrayList<HeroModel>) InternalStorage.readObject(getApplication(), STORAGE_KEY);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", getApplication()
                    .getResources().getString(R.string.file_read_error) + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("MainActivity", getApplication()
                    .getResources().getString(R.string.file_read_error) + e);
        }

        return null; // Cached file couldn't be loaded
    }

    public void setHeroList(ArrayList<HeroModel> heroList) {
        mHeroList.setValue(heroList);
    }

    public void removeHero(int heroPosition) {
        if (mHeroList.getValue() != null) {
            ArrayList<HeroModel> heroList = mHeroList.getValue();
            heroList.remove(heroPosition);

            saveChanges();
        }
    }

    public void moveHero(int fromPosition, int toPosition) {
        if (mHeroList.getValue() != null) {
            ArrayList<HeroModel> heroList = mHeroList.getValue();

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(heroList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(heroList, i, i - 1);
                }
            }

            saveChanges();
        }
    }

    public void addHero(HeroModel hero) {
        if (mHeroList.getValue() != null) {
            ArrayList<HeroModel> heroList = mHeroList.getValue();
            heroList.add(hero);

            saveChanges();
        }
    }

    private void saveChanges() {
        // Store the list into internal storage
        try {
            InternalStorage.writeObject(getApplication(), STORAGE_KEY, mHeroList.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", getApplication()
                    .getResources().getString(R.string.write_file_error) + e);
        }
    }



    public interface OnLoadedDataListener {

        void OnFinished();

        void onFailed(Exception e);
    }
}
