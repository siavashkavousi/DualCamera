package com.siavash.dualcamera;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CameraView.OnCaptureListener {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraView cameraView = new CameraView();
        switchFragment(R.id.container, cameraView);
    }

    private void switchFragment(int resId, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(resId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override public void onCaptureComplete() {
        PhotoView photoView = new PhotoView();
        switchFragment(R.id.container, photoView);
    }
}
