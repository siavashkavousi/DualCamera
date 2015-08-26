package com.siavash.dualcamera;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CameraBase.OnCaptureListener {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraFront cameraFront = new CameraFront();
        PhotoFragment photoFragment = new PhotoFragment();
        switchFragment(R.id.container, photoFragment);
    }

    private void switchFragment(int resId, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
        transaction.replace(resId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override public void onCaptureComplete(String frontBack) {
        switch (frontBack) {
            case Constants.CAMERA_FRONT:
                CameraBase cameraBack = new CameraBack();
                switchFragment(R.id.container, cameraBack);
                break;
            case Constants.CAMERA_BACK:
                PhotoFragment photoFragment = new PhotoFragment();
                switchFragment(R.id.container, photoFragment);
                break;
            default:
                // nothing
                break;
        }
    }
}
