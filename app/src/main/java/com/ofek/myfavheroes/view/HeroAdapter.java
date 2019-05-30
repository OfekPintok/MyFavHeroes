
/*
 * Created by Ofek Pintok on 5/30/19 8:24 AM
 */

package com.ofek.myfavheroes.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ofek.myfavheroes.R;
import com.ofek.myfavheroes.model.HeroModel;

import java.util.ArrayList;

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.ViewHolder> {

    public static final int DEFAULT_POSITION = -1;

    private ArrayList<HeroModel> mHeroList;
    private int checkedPosition;

    private RequestManager mGlide;
    private OnHeroClickListener mHeroListListener;

    HeroAdapter(ArrayList<HeroModel> heroList, Context context, int checkedPosition) {
        this.mHeroList = heroList;
        this.mHeroListListener = (OnHeroClickListener) context;
        this.mGlide = Glide.with(context);
        this.checkedPosition = checkedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hero, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final HeroModel heroModel = mHeroList.get(position);

        // Initialize each item with the following attributes
        String name = heroModel.getName();
        holder.nameTv.setText(name);

        String abilities = heroModel.getAbilitiesString();
        holder.abilitiesTv.setText(abilities);

        mGlide.load(heroModel.getImage())
                .placeholder(R.drawable.image_hero_not_loaded)
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);

                        Log.d("HeroAdapter", "Was unable to load image via glide: " + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.image_hero_not_loaded)
                .into(holder.heroIv);

        if (position == checkedPosition) {
            holder.favoriteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.favoriteIcon.setVisibility(View.INVISIBLE);
        }

        // Keep the image URL reference for late usage (as a main activity cover).
        holder.heroImageUrl = heroModel.getImage();
    }

    @Override
    public int getItemCount() {
        return mHeroList.size();
    }

    public HeroModel getItem(int position) {
        return mHeroList.get(position);
    }

    public void setCheckedPosition(int position) {
        int prevChecked = checkedPosition;
        checkedPosition = position;

        if (prevChecked != DEFAULT_POSITION) {
            notifyItemChanged(prevChecked);
        }
        notifyItemChanged(checkedPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTv;
        TextView abilitiesTv;
        ImageView heroIv;
        String heroImageUrl;
        ImageView favoriteIcon;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Create references for item's fields
            nameTv = itemView.findViewById(R.id.item_hero_name_tv);
            abilitiesTv = itemView.findViewById(R.id.item_hero_abilities_tv);
            heroIv = itemView.findViewById(R.id.item_hero_picture_iv);
            favoriteIcon = itemView.findViewById(R.id.item_hero_favorite_iv);
            progressBar = itemView.findViewById(R.id.item_hero_progressbar);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View itemView) {
            // Notify to the main that an item was chosen.
            mHeroListListener.onHeroClick(getAdapterPosition());

            // Update current checked position
            if (checkedPosition != getAdapterPosition()) {
                setCheckedPosition(getAdapterPosition());
            }
        }
    }

    public interface OnHeroClickListener {

        void onHeroClick(int position);

    }
}
