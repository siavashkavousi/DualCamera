package com.siavash.dualcamera;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.siavash.dualcamera.util.BitmapUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Class for editing photos before saving into file
 * Created by sia on 8/18/15.
 */
public class PhotoFragment extends Fragment {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    @Bind(R.id.photo_back) ImageView backPhoto;
    @Bind(R.id.photo_front) ImageView frontPhoto;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PhotoFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, view);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels, height = metrics.heightPixels;

        Bitmap backBitmap = BitmapUtil.decodeSampledBitmap(getActivity(), Constants.CAMERA_BACK_IMAGE_URL, width, height);
        Log.d(TAG, "back camera bitmap width: " + backBitmap.getWidth() + " and height: " + backBitmap.getHeight());
        Bitmap frontBitmap = BitmapUtil.decodeSampledBitmap(getActivity(), Constants.CAMERA_FRONT_IMAGE_URL, width / 4, height / 4);
        Log.d(TAG, "front camera bitmap width: " + frontBitmap.getWidth() + " and height: " + frontBitmap.getHeight());

        frontPhoto.setImageBitmap(frontBitmap);
        backPhoto.setImageBitmap(backBitmap);



        frontPhoto.setOnTouchListener(new OnTouchListener(backPhoto));

        return view;
    }

    private class OnTouchListener implements View.OnTouchListener {
        private ImageView backgroundImage;

        public OnTouchListener(ImageView backgroundImage){
            this.backgroundImage = backgroundImage;
        }

        @Override public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();

            float dx = v.getWidth() / 2;
            float dy = v.getHeight() / 2;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.setX(event.getRawX() - dx);
                    v.setY(event.getRawY() - dy);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
}
