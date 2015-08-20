package com.siavash.dualcamera;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by sia on 8/14/15.
 */
public class CameraFront extends CameraBase {
    private static final String TAG = CameraFront.class.getSimpleName();

    private static final int CAMERA_ID = 1;

    public CameraFront(CameraView cameraView, Context context, ViewGroup layout) {
        super(cameraView, context);
        Log.d(TAG, "CameraFront is created");
        safeCameraOpenInView(layout);
    }

    @Override protected boolean safeCameraOpenInView(ViewGroup viewLayout) {
        boolean qOpened;
        releaseCameraAndPreview();
        mCamera = getCameraInstance(CAMERA_ID);
        qOpened = (mCamera != null);

        if (qOpened) {
            mPreview = new CameraPreview(mContext, mCamera, viewLayout);
            viewLayout.addView(mPreview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }
}
