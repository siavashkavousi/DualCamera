package com.siavash.dualcamera;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.siavash.dualcamera.util.BitmapUtil;
import com.siavash.dualcamera.util.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * Class for editing photos before saving into file
 * Created by sia on 8/18/15.
 */
public class PhotoFragment extends Fragment implements Toolbar.OnClickListener, Observer {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    @Bind(R.id.photo_layout) RelativeLayout mPhotoLayout;
    @Bind(R.id.photo_back) ImageView mBackImageView;
    @Bind(R.id.photo_front) ImageView mFrontImageView;

    private int mWidth, mHeight;
    private boolean mSavedFrontBitmap, mSavedBackBitmap;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PhotoFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, view);
        // Set up toolbar
        Toolbar.Builder<PhotoFragment> builder = new Toolbar.Builder<>(getActivity(), this, view, true);
        builder.build();

        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
        mSavedFrontBitmap = false;
        mSavedBackBitmap = false;

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
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(Constants.CONTAINER_RES_ID, new CameraFront(this), Constants.FRONT_CAMERA_FRAGMENT).commit();
    }

    @Override public void onCompleted() {
        if (CameraBase.sFrontBack.equals(Constants.CAMERA_BACK)) {
            mSavedBackBitmap = true;
            setUpImageView();
        } else if (CameraBase.sFrontBack.equals(Constants.CAMERA_FRONT)) {
            mSavedFrontBitmap = true;
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

        mFrontImageView.post(new Runnable() {
            @Override public void run() {
                mFrontImageView.setY(200);
                mFrontImageView.setImageBitmap(frontBitmap);
            }
        });
        mBackImageView.post(new Runnable() {
            @Override public void run() {
                mBackImageView.setImageBitmap(backBitmap);
            }
        });


//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBackImageView.getLayoutParams();
//        layoutParams.width = backBitmap.getWidth();
//        layoutParams.height = backBitmap.getHeight();
//        mBackImageView.setLayoutParams(layoutParams);
        int[] location = new int[2];
        mBackImageView.getLocationOnScreen(location);
        Log.d(TAG, location[0] + " " + location[1] + " " + mBackImageView.getWidth() + " " + mBackImageView.getHeight()
                + " " + mBackImageView.getTop() + " " + mBackImageView.getBottom() + " " + mBackImageView.getLeft()
                + " " + mBackImageView.getRight());
        mFrontImageView.setOnTouchListener(new OnTouchListener(mBackImageView));
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
        private ImageView backgroundImageView;

        private OnTouchListener(ImageView backgroundImageView) {
            matrix = new Matrix();
            savedMatrix = new Matrix();
            this.backgroundImageView = backgroundImageView;
        }

        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);

            dumpEvent(event);

            // Handle touch events here...
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    startPoint.set(event.getX(), event.getY());
                    mode = DRAG;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDistance = spacing(event);
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
                    int[] loca = new int[2];
                    v.getLocationOnScreen(loca);
                    Log.d(TAG, "location: " + loca[0] + "    " + loca[1]);
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        v.setTranslationX(event.getRawX() - startPoint.x);
                        v.setTranslationY(event.getRawY() - startPoint.y);
                    } else if (mode == ZOOM && event.getPointerCount() == 2) {
                        float newDistance = spacing(event);
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

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return FloatMath.sqrt(x * x + y * y);
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
