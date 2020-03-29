/*
 * Created by Ofek Pintok on 8/1/19 4:22 PM
 */

package com.ofek.myfavheroes.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ofek.myfavheroes.R;
import com.ofek.myfavheroes.model.HeroModel;

import static com.ofek.myfavheroes.view.MainActivity.DETAILED_HERO_TAG;

public class DetailedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        initViews();
    }

    private void initViews() {
        TextView heroNameTv = findViewById(R.id.detailed_name_tv);
        TextView heroAbilitiesTv = findViewById(R.id.detailed_abilities_tv);
        ImageView heroIv = findViewById(R.id.detailed_cover_iv);
        Button backButton = findViewById(R.id.detailed_back_btn);

        HeroModel hero = (HeroModel) getIntent().getSerializableExtra(DETAILED_HERO_TAG);

        heroNameTv.setText(hero.getName());

        Glide.with(this)
                .load(hero.getImage())
                .into(heroIv);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        StringBuilder listOfAbilities = new StringBuilder("List Of Abilities:");
        for (int i = 0; i < hero.getAbilities().length; i++) {
            listOfAbilities
                    .append("\n")
                    .append(i+1)
                    .append(". ")
                    .append(hero.getAbilities()[i]);
        }
        heroAbilitiesTv.setText(listOfAbilities.toString());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
