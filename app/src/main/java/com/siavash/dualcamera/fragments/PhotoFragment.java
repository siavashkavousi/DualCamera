package com.siavash.dualcamera.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.R;
import com.siavash.dualcamera.util.BitmapUtil;
import com.siavash.dualcamera.util.customviews.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * Editing photos before saving into file or sharing with others
 * Created by sia on 8/18/15.
 */
public class PhotoFragment extends Fragment implements Toolbar.OnBackClickListener, Toolbar.OnActionClickListener, Observer {
    private static final String TAG = PhotoFragment.class.getSimpleName();
    private static PhotoFragment sPhotoFragment;

    @Bind(R.id.toolbar) Toolbar<PhotoFragment> toolbar;
    @Bind(R.id.photo_layout) RelativeLayout photoLayout;
    @Bind(R.id.photo_back) ImageView backImageView;
    @Bind(R.id.photo_front) ImageView frontImageView;

    private OnFragmentChange mCallback;
    private MaterialDialog progressDialog;
    private int mWidth, mHeight;
    private String mImageUrl;

    private PhotoFragment() {
    }

    public static PhotoFragment getInstance() {
        if (sPhotoFragment == null) {
            sPhotoFragment = new PhotoFragment();
        }
        return sPhotoFragment;
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnFragmentChange) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentChange");
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PhotoFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, view);
        // Set up toolbar
        toolbar.setTitle("ویرایش عکس");
        toolbar.setActionButtonVisibility(View.VISIBLE);
        toolbar.setCallback(this);

        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        progressDialog = new MaterialDialog.Builder(getActivity()).title("در حال بارگذاری").content("وایسا بچه").progress(true, 0).progressIndeterminateStyle(true).show();

        return view;
    }

    @Override public void onCompleted() {
        if (CameraBase.sFrontBack == Constants.PHOTO_FRAGMENT) {
            setUpImageView();
        }
    }

    @Override public void onError(Throwable e) {

    }

    @Override public void onNext(Object o) {

    }

    private void setUpImageView() {
        final Bitmap frontBitmap = BitmapUtil.decodeSampledBitmap(getActivity(), Constants.CAMERA_FRONT_IMAGE_URL, mWidth / 4, mHeight / 4);
        if (frontBitmap == null) throw new NullPointerException("Front bitmap is null");
        Log.d(TAG, "front camera bitmap width: " + frontBitmap.getWidth() + " and height: " + frontBitmap.getHeight());
        final Bitmap backBitmap = BitmapUtil.decodeSampledBitmap(getActivity(), Constants.CAMERA_BACK_IMAGE_URL, mWidth, mHeight);
        if (backBitmap == null) throw new NullPointerException("Back bitmap is null");
        Log.d(TAG, "back camera bitmap width: " + backBitmap.getWidth() + " and height: " + backBitmap.getHeight());

        frontImageView.post(new Runnable() {
            @Override public void run() {
                frontImageView.setY(200);
                frontImageView.setImageBitmap(frontBitmap);
            }
        });
        backImageView.post(new Runnable() {
            @Override public void run() {
                backImageView.setImageBitmap(backBitmap);
            }
        });

        progressDialog.dismiss();

        int[] location = new int[2];
        backImageView.getLocationOnScreen(location);
        Log.d(TAG, location[0] + " " + location[1] + " " + backImageView.getWidth() + " " + backImageView.getHeight()
                + " " + backImageView.getTop() + " " + backImageView.getBottom() + " " + backImageView.getLeft()
                + " " + backImageView.getRight());
        frontImageView.setOnTouchListener(new OnTouchListener(backImageView));
    }

    @Override public void goBack() {
        mCallback.switchFragmentTo(Constants.CAMERA_FRONT_FRAGMENT);
    }

    @Override public void doAction() {
        photoLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = photoLayout.getDrawingCache();
        mImageUrl = BitmapUtil.save(getActivity(), bitmap, BitmapUtil.setImageFile());
        photoLayout.setDrawingCacheEnabled(false);
        mCallback.switchFragmentTo(Constants.SHARE_FRAGMENT, mImageUrl);
    }

    private class OnTouchListener implements View.OnTouchListener {
        private static final int NONE = 0;
        private static final int DRAG = 1;
        private static final int ZOOM = 2;
        private final String TAG = OnTouchListener.class.getSimpleName();
        float[] lastEvent = null;
        float angle = 0f;
        float newRotation = 0f;
        float oldDistance = 1f;
        private Matrix matrix;
        private Matrix savedMatrix;

        private int mode = NONE;
        // Remember some things for zooming
        private PointF startPoint = new PointF();
        private PointF mid = new PointF();
        // fields to limit movement of the front camera image
        private ImageView backgroundImageView;
        private int centerX, centerY;
        private float dx, dy, dz, dw, x, y, z, w;

        private OnTouchListener(ImageView backgroundImageView) {
            matrix = new Matrix();
            savedMatrix = new Matrix();
            this.backgroundImageView = backgroundImageView;

            centerX = mWidth / 2;
            centerY = mHeight / 2;
        }

        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);

            dumpEvent(event);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

            // Handle touch events here...
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    dx = event.getRawX() - layoutParams.leftMargin;
                    dy = event.getRawY() - layoutParams.topMargin;
                    dz = event.getRawX() - layoutParams.bottomMargin;
                    dw = event.getRawX() - layoutParams.rightMargin;

                    savedMatrix.set(matrix);
                    startPoint.set(event.getX(), event.getY());
                    mode = DRAG;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDistance = (float) spacing(event);
                    if (oldDistance > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    angle = rotate(event);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);

                        x = event.getRawX();
                        y = event.getRawY();

                        layoutParams.leftMargin = (int) (x - dx);
                        layoutParams.topMargin = (int) (y - dy);
                        layoutParams.bottomMargin = (int) (z - dz);
                        layoutParams.rightMargin = (int) (w - dw);

                        view.setLayoutParams(layoutParams);
                    } else if (mode == ZOOM && event.getPointerCount() == 2) {
                        float newDistance = (float) spacing(event);
                        matrix.set(savedMatrix);
                        if (newDistance > 20f) {
                            float scale = newDistance / oldDistance;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                        if (lastEvent != null) {
                            newRotation = rotate(event);
                            float r = newRotation - angle;
                            matrix.postRotate(r, view.getMeasuredWidth() / 2, view.getMeasuredHeight() / 2);
                        }
                    }
                    break;
            }

            view.setImageMatrix(matrix);

            return true;
        }

        private float rotate(MotionEvent event) {
            return (float) Math.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1)));
        }

        private double spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.sqrt(x * x + y * y);
        }

        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }

        private void dumpEvent(MotionEvent event) {
            String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                    "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
            StringBuilder sb = new StringBuilder();
            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            sb.append("event ACTION_").append(names[actionCode]);
            if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                    || actionCode == MotionEvent.ACTION_POINTER_UP) {
                sb.append("(pid ").append(
                        action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                sb.append(")");
            }

            sb.append("[");

            for (int i = 0; i < event.getPointerCount(); i++) {
                sb.append("#").append(i);
                sb.append("(pid ").append(event.getPointerId(i));
                sb.append(")=").append((int) event.getX(i));
                sb.append(",").append((int) event.getY(i));
                if (i + 1 < event.getPointerCount())

                    sb.append(";");
            }

            sb.append("]");
            Log.d(TAG, sb.toString());
        }
    }
}
