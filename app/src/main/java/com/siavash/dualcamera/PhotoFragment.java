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
import com.siavash.dualcamera.util.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Class for editing photos before saving into file
 * Created by sia on 8/18/15.
 */
public class PhotoFragment extends Fragment implements Toolbar.OnClickListener {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    @Bind(R.id.photo_back) ImageView mBackImageView;
    @Bind(R.id.photo_front) ImageView mFrontImageView;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PhotoFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, view);
        // Set up toolbar
        Toolbar toolbar = new Toolbar.Builder<>(this, R.id.toolbar, view).setTitle("ویرایش عکس").build();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels, height = metrics.heightPixels;

        Bitmap backBitmap = BitmapUtil.decodeSampledBitmap(getActivity(), Constants.CAMERA_BACK_IMAGE_URL, width, height);
        if (backBitmap == null) throw new NullPointerException("Back bitmap is null");
        Log.d(TAG, "back camera bitmap width: " + backBitmap.getWidth() + " and height: " + backBitmap.getHeight());
        Bitmap frontBitmap = BitmapUtil.decodeSampledBitmap(getActivity(), Constants.CAMERA_FRONT_IMAGE_URL, width / 4, height / 4);
        if (frontBitmap == null) throw new NullPointerException("Front bitmap is null");
        Log.d(TAG, "front camera bitmap width: " + frontBitmap.getWidth() + " and height: " + frontBitmap.getHeight());

        mFrontImageView.setImageBitmap(frontBitmap);
        mBackImageView.setImageBitmap(backBitmap);

        mFrontImageView.setOnTouchListener(new OnTouchListener(mBackImageView));

        return view;
    }

    private void saveFile(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        BitmapUtil.save(getActivity(), bitmap);
    }

    @Override public void nextButtonOnClick() {
        saveFile(getView());
    }

    @Override public void backButtonOnClick() {

    }

    private class OnTouchListener implements View.OnTouchListener {
        private ImageView backgroundImage;

        public OnTouchListener(ImageView backgroundImage) {
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
