/*
 * Created by Ofek Pintok on 5/30/19 8:25 AM
 */

package com.ofek.myfavheroes.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeroResponse {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("abilities")
    @Expose
    private List<String> abilities = null;
    @SerializedName("image")
    @Expose
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getAbilities() {

        String[] abilitiesArray = new String[abilities.size()];
        abilities.toArray(abilitiesArray);

        return abilitiesArray;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}