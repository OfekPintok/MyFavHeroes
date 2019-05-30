/*
 * Created by Ofek Pintok on 5/30/19 8:25 AM
 */

package com.ofek.myfavheroes.model;

import java.io.Serializable;

public class HeroModel implements Serializable {

    private String mName;
    private String[] mAbilities;
    private String mImage;

    public HeroModel(String mName, String[] mAbilities, String mImage) {
        this.mName = mName;
        this.mAbilities = mAbilities;
        this.mImage = mImage;
    }

    public String getName() {
        return mName;
    }

    public String[] getAbilities() {
        return mAbilities;
    }

    public String getAbilitiesString() {
        String abilitiesStr = "";
        int numOfAbilities = mAbilities.length;

        for (int i = 0; i < numOfAbilities; i++) {
            abilitiesStr = abilitiesStr.concat(mAbilities[i]);

            // concat ", " for every ability, excluding the last ability in the array.
            if (i < numOfAbilities - 1) {
                abilitiesStr = abilitiesStr.concat(", ");
            }
        }
        return abilitiesStr;
    }

    public String getImage() {
        return mImage;
    }
}
