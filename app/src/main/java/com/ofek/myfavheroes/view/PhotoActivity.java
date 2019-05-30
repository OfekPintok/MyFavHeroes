
/*
 * Created by Ofek Pintok on 5/30/19 8:28 AM
 */

package com.ofek.myfavheroes.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.ofek.myfavheroes.R;

public class PhotoActivity extends Activity {

    public static final String PICTURE_TAG = "PICTURE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        byte[] byteArray = getIntent().getByteArrayExtra(PICTURE_TAG);

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        PhotoView photo = findViewById(R.id.fullscreen_image);
        photo.setImageBitmap(bitmap);
    }
}
