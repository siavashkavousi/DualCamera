package com.siavash.dualcamera;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.siavash.dualcamera.util.FragmentUtil;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnFragmentChange {

    private PhotoFragment mPhotoFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPhotoFragment = PhotoFragment.getInstance();

        CameraBase cameraFront = new CameraFront(mPhotoFragment);
        FragmentUtil.switchFragment(getFragmentManager(), Constants.CONTAINER_RES_ID, cameraFront);
    }

    @Override public void switchFragmentTo(int index, String... optionalValues) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (index) {
            case Constants.CAMERA_FRONT_FRAGMENT:
                CameraBase cameraFront = new CameraFront(mPhotoFragment);
                FragmentUtil.switchFragment(fragmentManager, Constants.CONTAINER_RES_ID, cameraFront);
                break;
            case Constants.CAMERA_BACK_FRAGMENT:
                CameraBase cameraBack = new CameraBack(mPhotoFragment);
                FragmentUtil.switchFragment(fragmentManager, Constants.CONTAINER_RES_ID, cameraBack);
                break;
            case Constants.PHOTO_FRAGMENT:
                FragmentUtil.switchFragment(fragmentManager, Constants.CONTAINER_RES_ID, mPhotoFragment);
                break;
            case Constants.SHARE_FRAGMENT:
                ShareFragment shareFragment = ShareFragment.newInstance(optionalValues[0]);
                FragmentUtil.switchFragment(fragmentManager, Constants.CONTAINER_RES_ID, shareFragment);
                break;
        }
    }
}
