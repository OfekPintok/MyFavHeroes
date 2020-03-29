/*
 * Created by Ofek Pintok on 7/31/19 6:36 PM
 */

package com.ofek.myfavheroes.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ofek.myfavheroes.R;
import com.ofek.myfavheroes.model.HeroModel;

import java.io.File;

import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CreateHeroDialog extends DialogFragment {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String HERO_LISTENER = "HeroListenerTAG";
    private static final int WRITE_REQUEST_CODE = 1;

    private EditText mNameEt, mAbilitiesEt;
    private ImageView mPicture;
    private File mPictureFile;

    private CreateHeroInterface listener;

    static CreateHeroDialog newInstance(CreateHeroInterface listener) {

        Bundle args = new Bundle();
        CreateHeroDialog fragment = new CreateHeroDialog();

        args.putParcelable(HERO_LISTENER, listener);


        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_create_hero, container, false);

        if (getArguments() != null) {
            listener = getArguments().getParcelable(HERO_LISTENER);
        }

        // References
        mNameEt = view.findViewById(R.id.create_hero_name_et);
        mAbilitiesEt = view.findViewById(R.id.create_hero_abilities_et);
        mPicture = view.findViewById(R.id.create_hero_preview_im);

        /*// First init
        if (getActivity() != null) {
            mPicturePath = Uri.parse("android.resource://" + getActivity().getPackageName() +
                    "/" + R.drawable.image_hero_not_loaded).toString();
        }*/

        initCloseButton(view);
        initFinishButton(view);
        initUploadButton(view);
        getPermission();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && getActivity() != null && dialog.getWindow() != null) {
            // Retrieve display dimensions
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            dialog.getWindow().setLayout((int)(displayRectangle.width() * 0.9f), (int)(displayRectangle.height() * 0.95f));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = mPictureFile.getPath();
            Bitmap image = BitmapFactory.decodeFile(path);
            mPicture.setImageBitmap(image);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUploadButton(View view) {
        Button uploadImageBtn = view.findViewById(R.id.create_hero_upload_btn);
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, getUpdatedFileUri());
                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
            }
        });
    }

    private Uri getUpdatedFileUri() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String fileName = "IMG_" + formatter.format(calendar.getTime()) + ".jpg";

        mPictureFile =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);

        return Uri.fromFile(mPictureFile);
    }

    private void initCloseButton(View view) {
        ImageButton closeBtn = view.findViewById(R.id.create_hero_cancel_ib);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateHeroDialog.this.dismiss();
            }
        });
    }


    private void initFinishButton(View view) {
        ImageButton finishBtn = view.findViewById(R.id.create_hero_finish_ib);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String heroName = mNameEt.getText().toString();
                String heroAbilitiesFullStr = mAbilitiesEt.getText().toString();

                if (heroName.isEmpty() || heroAbilitiesFullStr.isEmpty()) {
                    Toast.makeText(getContext(), "Fill all details", Toast.LENGTH_SHORT).show();
                } else {

                    String[] heroAbilities = heroAbilitiesFullStr.split(",| ,|, | , ");
                    HeroModel hero = new HeroModel(heroName, heroAbilities, mPictureFile.getAbsolutePath());
                    listener.insertNewHero(hero);
                    CreateHeroDialog.this.dismiss();
                }
            }
        });
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity() != null) {
                int writePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writePermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                }
            }
        }
    }

    interface CreateHeroInterface extends Parcelable {
        void insertNewHero(HeroModel hero);

        @Override
        int describeContents();

        @Override
        void writeToParcel(Parcel dest, int flags);
    }
}
