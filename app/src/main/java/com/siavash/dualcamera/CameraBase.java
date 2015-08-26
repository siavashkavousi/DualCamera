package com.siavash.dualcamera;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.siavash.dualcamera.util.BitmapUtil;


/**
 * Parent class for camera controllers
 * Created by sia on 8/14/15.
 */
public abstract class CameraBase extends Fragment {
    private static final String TAG = CameraBase.class.getSimpleName();
    // Native camera.
    protected Camera mCamera;
    // View to display the camera output.
    protected CameraPreview mPreview;
    // camera picture callback
    protected PictureCallback mPictureCallback;
    // picture url
    private String mUrl;
    private String mFrontBack;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CameraBase onCreate method called");
        mPictureCallback = new PictureCallback();
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
        mFrontBack = frontBack;
        mCamera.takePicture(null, null, mPictureCallback);
    }

    @Override public void onDetach() {
        releaseCameraAndPreview();
        super.onDetach();
    }

    

    private class PictureCallback implements Camera.PictureCallback {
        private OnCaptureListener mCallback;

        public PictureCallback() {
            mCallback = (OnCaptureListener) getActivity();
        }

        @Override public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken called! saving into file is about to start");
            if (mUrl.isEmpty()) return;
            BitmapUtil.save(getActivity(), data, mUrl, Constants.DISPLAY_ORIENTATION);

            releaseCameraAndPreview();
            mCallback.onCaptureComplete(mFrontBack);
        }
    }

    public interface OnCaptureListener {
        void onCaptureComplete(String frontBack);
    }
}
