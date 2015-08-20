package com.siavash.dualcamera;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sia on 8/18/15.
 */
public class PhotoView extends Fragment {
    private static final String TAG = PhotoView.class.getSimpleName();

    @Bind(R.id.photo_back) ImageView backPhoto;
    @Bind(R.id.photo_front) ImageView frontPhoto;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_photo, container, false);
        ButterKnife.bind(this, view);

        Bitmap bitmap = IO.loadBitmap(getActivity(), Constants.CAMERA_FRONT_IMAGE_URL);
        frontPhoto.setImageBitmap(bitmap);
        bitmap = IO.loadBitmap(getActivity(), Constants.CAMERA_BACK_IMAGE_URL);
        backPhoto.setImageBitmap(bitmap);

        return view;
    }
}
