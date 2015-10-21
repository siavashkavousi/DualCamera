package com.siavash.dualcamera.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.control.CameraPreview;
import com.siavash.dualcamera.util.Util;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Parent class for camera controllers
 * Created by sia on 8/14/15.
 */
public abstract class CameraBase extends Fragment {
    private static final String TAG = CameraBase.class.getSimpleName();
    public static int frontBack;
    private static String url;
    protected Camera camera;
    protected CameraPreview preview;
    protected PictureCallback pictureCallback;
    private PhotoFragment photoFragment;

    public CameraBase(PhotoFragment photoFragment) {
        this.photoFragment = photoFragment;
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
        pictureCallback = new PictureCallback(getActivity(), photoFragment);
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
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (preview != null) {
            preview.destroyDrawingCache();
            preview.setCamera(null);
        }
    }

    public void takePicture(String url, int frontBack) {
        CameraBase.url = url;
        CameraBase.frontBack = frontBack;
        camera.takePicture(null, null, pictureCallback);
    }

    @Override public void onDetach() {
        releaseCameraAndPreview();
        super.onDetach();
    }

    private static class PictureCallback implements Camera.PictureCallback {
        private WeakReference<Activity> activity;
        private WeakReference<PhotoFragment> photoFragment;
        private OnFragmentInteractionListener callback;

        public PictureCallback(Activity activity, PhotoFragment photoFragment) {
            this.activity = new WeakReference<>(activity);
            this.photoFragment = new WeakReference<>(photoFragment);
            try {
                callback = (OnFragmentInteractionListener) this.activity.get();
            } catch (ClassCastException e) {
                throw new ClassCastException(this.activity.get().toString() + " must implement OnCaptureListener");
            }
        }

        @Override public void onPictureTaken(byte[] data, Camera camera) {
            if (Constants.IS_DEBUG)
                Log.d(TAG, "onPictureTaken called! saving into file is about to start");
            if (url.isEmpty()) return;

            Observable.create(subscriber -> {
                Util.save(activity.get(), data, frontBack, url, Constants.DISPLAY_ORIENTATION);
                subscriber.onCompleted();
            }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(photoFragment.get());

            callback.switchFragmentTo(frontBack);
        }
    }
}
