//package com.siavash.dualcamera.fragments;
//
//import android.hardware.Camera;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//
//import com.siavash.dualcamera.control.CameraPreview;
//import com.siavash.dualcamera.Constants;
//import com.siavash.dualcamera.R;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * Back camera controller
// * Created by sia on 8/14/15.
// */
//public class CameraBack extends CameraBase {
//    private static final String TAG = CameraBack.class.getSimpleName();
//
//    private static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;
//    @Bind(R.id.container) FrameLayout mFrameLayout;
//
//    public CameraBack(PhotoFragment photoFragment) {
//        super(photoFragment);
//    }
//
//    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_camera, container, false);
//        ButterKnife.bind(this, view);
//        safeCameraOpenInView(mFrameLayout);
//        return view;
//    }
//
//    @Override protected boolean safeCameraOpenInView(ViewGroup viewLayout) {
//        boolean qOpened;
//        releaseCameraAndPreview();
//        camera = getCameraInstance(CAMERA_ID);
//        qOpened = (camera != null);
//
//        if (qOpened) {
//            preview = new CameraPreview(getActivity(), camera, viewLayout);
//            viewLayout.addView(preview);
//            preview.startCameraPreview();
//        }
//        return qOpened;
//    }
//
//    @OnClick(R.id.shutter_btn) void shutterOnClick() {
//        takePicture(Constants.CAMERA_BACK_IMAGE_URL, Constants.PHOTO_FRAGMENT);
//    }
//}
