package com.siavash.dualcamera;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.siavash.dualcamera.util.BitmapUtil;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;


/**
 * Parent class for camera controllers
 * Created by sia on 8/14/15.
 */
public abstract class CameraBase extends Fragment {
    private static final String TAG = CameraBase.class.getSimpleName();
    public static String sFrontBack;
    // picture url
    private static String mUrl;
    // Native camera.
    protected Camera mCamera;
    // View to display the camera output.
    protected CameraPreview mPreview;
    // camera picture callback
    protected PictureCallback mPictureCallback;
    // photo fragment instance in order to observe saving image bitmaps
    private PhotoFragment mPhotoFragment;

    public CameraBase(PhotoFragment photoFragment){
        mPhotoFragment = photoFragment;
    }

    /**
     * Safe method for getting a camera instance.
     *
     * @param id camera id backCamera = 0, frontCamera = 1
     * @return camera instance
     */
    protected static Camera getCameraInstance(int id) {
        Camera c = null;
        try {
            c = Camera.open(id); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CameraBase onCreate method called");
        mPictureCallback = new PictureCallback(getActivity(), mPhotoFragment);
    }

    /**
     * Recommended "safe" way to open the camera.
     *
     * @param viewLayout
     * @return
     */
    protected abstract boolean safeCameraOpenInView(ViewGroup viewLayout);

    /**
     * Clear any existing preview / camera.
     */
    protected void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.setCamera(null);
        }
    }

    public void takePicture(String url, String frontBack) {
        mUrl = url;
        sFrontBack = frontBack;
        mCamera.takePicture(null, null, mPictureCallback);
    }

    @Override public void onDetach() {
        releaseCameraAndPreview();
        super.onDetach();
    }

    public interface OnCaptureListener {
        void onCaptureComplete(String frontBack);
    }

    private static class PictureCallback implements Camera.PictureCallback {
        private final WeakReference<Activity> mActivity;
        private final WeakReference<PhotoFragment> mPhotoFragment;
        private OnCaptureListener mCallback;

        public PictureCallback(Activity activity, PhotoFragment photoFragment) {
            mActivity = new WeakReference<>(activity);
            mPhotoFragment = new WeakReference<>(photoFragment);
            try {
                mCallback = (OnCaptureListener) mActivity.get();
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.get().toString() + " must implement OnCaptureListener");
            }
        }

        @Override public void onPictureTaken(final byte[] data, final Camera camera) {
            Log.d(TAG, "onPictureTaken called! saving into file is about to start");
            if (mUrl.isEmpty()) return;

            Subscription subscription = Observable.create(new Observable.OnSubscribe<Void>() {
                @Override public void call(Subscriber<? super Void> subscriber) {
                    BitmapUtil.save(mActivity.get(), data, mUrl, Constants.DISPLAY_ORIENTATION);
                    Log.d(TAG, Thread.currentThread().toString());
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.computation()).subscribe(mPhotoFragment.get());
            ApplicationBase.getRefWatcher(mActivity.get()).watch(subscription);

            mCallback.onCaptureComplete(sFrontBack);
        }
    }
}
