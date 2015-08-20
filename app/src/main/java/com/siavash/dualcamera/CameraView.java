package com.siavash.dualcamera;

import android.app.Activity;
import android.app.Fragment;
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
 * Created by sia on 8/14/15.
 */
public class CameraView extends Fragment implements OnPhotoSaved {
    private static final String TAG = CameraView.class.getSimpleName();

    @Bind(R.id.camera_front) FrameLayout cameraFrontContainer;
    @Bind(R.id.camera_back) FrameLayout cameraBackContainer;

    private OnCaptureListener mCallback;
    private CameraFront mCameraFront;
    private CameraBack mCameraBack;
    private boolean isBack, isCaptureComplete;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnCaptureListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCaptureListener");
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_camera, container, false);
        ButterKnife.bind(this, view);

        mCameraBack = new CameraBack(this, getActivity(), cameraBackContainer);
        isBack = true;
        isCaptureComplete = true;

        return view;
    }

    @OnClick(R.id.shutter_btn) void shutterOnClick() {
        if (isCaptureComplete) {
            if (isBack) {
                mCameraBack.takePicture(Constants.CAMERA_BACK_IMAGE_URL);
                isBack = false;
            } else {
                mCameraFront.takePicture(Constants.CAMERA_FRONT_IMAGE_URL);
                isBack = true;
                isCaptureComplete = false;
            }
        } else {
            mCallback.onCaptureComplete();
        }
    }

    @Override public void onPhotoSavedComplete() {
        mCameraFront = new CameraFront(this, getActivity(), cameraFrontContainer);
    }

    public interface OnCaptureListener {
        void onCaptureComplete();
    }
}
