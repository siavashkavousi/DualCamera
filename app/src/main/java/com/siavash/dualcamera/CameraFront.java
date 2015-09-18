package com.siavash.dualcamera;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Front camera controller
 * Created by sia on 8/14/15.
 */
public class CameraFront extends CameraBase {
    private static final String TAG = CameraFront.class.getSimpleName();

    private static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    @Bind(R.id.camera_container) FrameLayout mFrameLayout;

    public CameraFront(PhotoFragment photoFragment) {
        super(photoFragment);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, view);
        safeCameraOpenInView(mFrameLayout);
        return view;
    }

    protected boolean safeCameraOpenInView(ViewGroup viewLayout) {
        boolean qOpened;
        releaseCameraAndPreview();
        mCamera = getCameraInstance(CAMERA_ID);
        qOpened = (mCamera != null);

        if (qOpened) {
            mPreview = new CameraPreview(getActivity(), mCamera, viewLayout);
            viewLayout.addView(mPreview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    @OnClick(R.id.shutter_btn) void shutterOnClick() {
        takePicture(Constants.CAMERA_FRONT_IMAGE_URL, Constants.CAMERA_BACK_FRAGMENT);
    }
}
