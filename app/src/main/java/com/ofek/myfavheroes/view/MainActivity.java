
/*
 * Created by Ofek Pintok on 5/29/19 8:23 AM
 */

package com.ofek.myfavheroes.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.ofek.myfavheroes.model.HeroModel;
import com.ofek.myfavheroes.storage.InternalStorage;
import com.ofek.myfavheroes.viewmodel.HeroViewModel;
import com.ofek.myfavheroes.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.ofek.myfavheroes.view.PhotoActivity.PICTURE_TAG;

public class MainActivity extends AppCompatActivity implements HeroAdapter.OnHeroClickListener, View.OnClickListener {

    private static final String PREF_TAG = "Shared_PREF_File";
    private static final String FAV_HERO_TAG = "My_Favorite_Hero";
    private static final String STORAGE_KEY = "Internal_Storage_Key";

    private HeroViewModel mHeroViewModel;
    private RecyclerView mRecyclerView;
    private HeroAdapter mHeroAdapter;

    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to Progress Bar
        progressBar = findViewById(R.id.main_content_progressBar);

        // Initialize Collapsing Toolbar attributes
        collapsingToolbarLayout = findViewById(R.id.main_collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedToolbarLayoutTitleColor);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedToolbarLayoutTitleColor);

        // Initialize View Model
        mHeroViewModel = ViewModelProviders.of(this).get(HeroViewModel.class);

        // Initialize Recycler View
        mRecyclerView = findViewById(R.id.main_content_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);

        mHeroViewModel.getObservableHeroList().observe(this, new Observer<ArrayList<HeroModel>>() {
            @Override
            public void onChanged(ArrayList<HeroModel> heroList) {

                if (heroList != null) {

                    // Load recorded data from Shared Preference file
                    SharedPreferences sp = getSharedPreferences(PREF_TAG, MODE_PRIVATE);
                    int checkedPosition = sp.getInt(FAV_HERO_TAG, HeroAdapter.DEFAULT_POSITION);

                    // On hero list change, update Recycler View data
                    mHeroAdapter = new HeroAdapter(heroList, MainActivity.this, checkedPosition);
                    mRecyclerView.setAdapter(mHeroAdapter);

                    // Load checked position model
                    if (checkedPosition != HeroAdapter.DEFAULT_POSITION) {
                        HeroModel heroModel = mHeroAdapter.getItem(checkedPosition);
                        changeCover(heroModel.getImage(), heroModel.getName());
                    } else {
                        // Before picking up a hero for the first time
                        Toolbar toolbar = findViewById(R.id.main_toolbar);
                        toolbar.setTitle(getResources().getString(R.string.default_title));
                        setSupportActionBar(toolbar);

                        ImageView mainCoverImage = findViewById(R.id.main_cover_iv);
                        mainCoverImage.setImageDrawable(getResources().getDrawable(R.drawable.image_unknown_hero));
                        mainCoverImage.setOnClickListener(MainActivity.this);
                    }

                    // Store the list into internal storage
                    try {
                        InternalStorage.writeObject(MainActivity.this, STORAGE_KEY, heroList);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("MainActivity", "Couldn't write new file " + e);
                    }
                }
            }
        });

        if (savedInstanceState == null) {
            // Happens once when activity is created
            showProgress();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mHeroViewModel.loadData(new HeroViewModel.OnLoadedDataListener() {
            @Override
            public void OnFinished() {
                hideProgress();
            }

            @Override
            public void onFailed(Exception e) {
                hideProgress();

                // Load the list from the cached storage
                ArrayList<HeroModel> cachedHeroList = getCachedData();
                mHeroViewModel.setHeroList(cachedHeroList);

                // Cached file couldn't be loaded
                if (cachedHeroList == null) {
                    displayError(e);
                }
            }
        });
    }

    /*
     * @param position - The item position that was clicked
     */
    @Override
    public void onHeroClick(int position) {

        // Scroll to the top of the RecyclerView
        if (mRecyclerView.getLayoutManager() != null) {
            while (!mRecyclerView.getLayoutManager().isSmoothScrolling()) {
                mRecyclerView.smoothScrollToPosition(0);
            }
            AppBarLayout appBarLayout = findViewById(R.id.main_appbarlayout);
            appBarLayout.setExpanded(true, true);
        }

        String imageUrl = mHeroAdapter.getItem(position).getImage();
        String heroName = mHeroAdapter.getItem(position).getName();
        changeCover(imageUrl, heroName);

        // Save last checked position in the Shared Preference file
        SharedPreferences.Editor editor = getSharedPreferences(PREF_TAG, MODE_PRIVATE).edit();
        editor.putInt(FAV_HERO_TAG, position).apply();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.main_cover_iv) {
            // Downcast View into ImageView, then cast it into Bitmap
            Bitmap bitmap = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();

            // Covert bitmap into Bytecode
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent imageIntent = new Intent(MainActivity.this, PhotoActivity.class);
            imageIntent.putExtra(PICTURE_TAG, byteArray);
            startActivity(imageIntent);
        }
    }

    private ArrayList<HeroModel> getCachedData() {
        try {
            return (ArrayList<HeroModel>) InternalStorage.readObject(this, STORAGE_KEY);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", "Couldn't load cached data for the reason:" + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("MainActivity", "Couldn't load cached data for the reason:" + e);
        }

        return null; // Cached file couldn't be loaded
    }

    private void changeCover(String imageUrl, String heroName) {
        // Main Cover references
        ImageView mainCoverImage = findViewById(R.id.main_cover_iv);

        // Update Main Cover
        collapsingToolbarLayout.setTitle(heroName);
        Glide.with(this)
                .load(imageUrl)
                .into(mainCoverImage);

        mainCoverImage.setOnClickListener(this);
        Log.i("MainActivity", "Cover was changed");
    }

    private void displayError(Exception e) {
        if (e instanceof NetworkErrorException) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

            ImageView noConnectionIv = findViewById(R.id.main_no_connection_iv);
            noConnectionIv.setVisibility(View.VISIBLE);
            collapsingToolbarLayout.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Error was found: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
