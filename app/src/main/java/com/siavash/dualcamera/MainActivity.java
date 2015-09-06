package com.siavash.dualcamera;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CameraBase.OnCaptureListener {

    private PhotoFragment mPhotoFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPhotoFragment = new PhotoFragment();

        CameraBase cameraFront = new CameraFront(mPhotoFragment);
        switchFragment(Constants.CONTAINER_RES_ID, cameraFront, Constants.FRONT_CAMERA_FRAGMENT);
    }

    public void switchFragment(int resId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right, R.anim.slide_in_right, R.anim.slide_out_right);
        transaction.replace(resId, fragment, tag);
        transaction.commit();
    }

    @Override public void onCaptureComplete(String frontBack) {
        switch (frontBack) {
            case Constants.CAMERA_FRONT:
                CameraBase cameraBack = new CameraBack(mPhotoFragment);
                switchFragment(Constants.CONTAINER_RES_ID, cameraBack, Constants.BACK_CAMERA_FRAGMENT);
                break;
            case Constants.CAMERA_BACK:
                switchFragment(Constants.CONTAINER_RES_ID, mPhotoFragment, Constants.PHOTO_FRAGMENT);
                break;
            default:
                // nothing
                break;
        }
    }
}
