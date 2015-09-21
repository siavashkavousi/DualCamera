package com.siavash.dualcamera.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.siavash.dualcamera.control.CameraPreview;
import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.util.BitmapUtil;

import java.lang.ref.WeakReference;


/**
 * Parent class for camera controllers
 * Created by sia on 8/14/15.
 */
public abstract class CameraBase extends Fragment {
    private static final String TAG = CameraBase.class.getSimpleName();
    public static int sFrontBack;
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

    public CameraBase(PhotoFragment photoFragment) {
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

    public void takePicture(String url, int frontBack) {
        mUrl = url;
        sFrontBack = frontBack;
        mCamera.takePicture(null, null, mPictureCallback);
    }

    @Override public void onDetach() {
        releaseCameraAndPreview();
        super.onDetach();
    }

    private static class PictureCallback implements Camera.PictureCallback {
        private WeakReference<Activity> mActivity;
        private WeakReference<PhotoFragment> mPhotoFragment;
        private OnFragmentChange mCallback;

        public PictureCallback(Activity activity, PhotoFragment photoFragment) {
            mActivity = new WeakReference<>(activity);
            mPhotoFragment = new WeakReference<>(photoFragment);
            try {
                mCallback = (OnFragmentChange) mActivity.get();
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.get().toString() + " must implement OnCaptureListener");
            }
        }

        @Override public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken called! saving into file is about to start");
            if (mUrl.isEmpty()) return;

            BitmapUtil.save(mActivity.get(), data, mUrl, Constants.DISPLAY_ORIENTATION, mPhotoFragment.get());
            mCallback.switchFragmentTo(sFrontBack);
        }
    }
}
