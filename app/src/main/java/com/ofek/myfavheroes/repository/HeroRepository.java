/*
 * Created by Ofek Pintok on 5/30/19 8:24 AM
 */

package com.ofek.myfavheroes.repository;


import com.ofek.myfavheroes.network.NetworkComponent;

public class HeroRepository {

    private static HeroRepository instance;
    private NetworkComponent networkComponent;

    private HeroRepository() {
        networkComponent = new NetworkComponent();
    }

    public static HeroRepository getInstance() {

        if (instance == null) {
            instance = new HeroRepository();
        }

        return instance;
    }

    public void getHeroList (NetworkComponent.OnReceivedDataListener callListener) {
        networkComponent.getHeroes(callListener);
    }

}
