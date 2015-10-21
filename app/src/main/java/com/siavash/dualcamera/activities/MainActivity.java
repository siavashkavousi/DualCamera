package com.siavash.dualcamera.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.R;
import com.siavash.dualcamera.fragments.CameraBack;
import com.siavash.dualcamera.fragments.CameraBase;
import com.siavash.dualcamera.fragments.CameraFront;
import com.siavash.dualcamera.fragments.OnFragmentInteractionListener;
import com.siavash.dualcamera.fragments.PhotoFragment;
import com.siavash.dualcamera.fragments.ShareFragment;
import com.siavash.dualcamera.util.FragmentUtil;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private PhotoFragment mPhotoFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPhotoFragment = PhotoFragment.getInstance();

        CameraBase cameraFront = new CameraFront(mPhotoFragment);
        FragmentUtil.replaceFragment(getFragmentManager(), Constants.CONTAINER_RES_ID, cameraFront);
    }

    @Override public void switchFragmentTo(int index, String... optionalValues) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (index) {
            case Constants.CAMERA_FRONT_FRAGMENT:
                CameraBase cameraFront = new CameraFront(mPhotoFragment);
                FragmentUtil.replaceFragment(fragmentManager, Constants.CONTAINER_RES_ID, cameraFront);
                break;
            case Constants.CAMERA_BACK_FRAGMENT:
                CameraBase cameraBack = new CameraBack(mPhotoFragment);
                FragmentUtil.replaceFragment(fragmentManager, Constants.CONTAINER_RES_ID, cameraBack);
                break;
            case Constants.PHOTO_FRAGMENT:
                FragmentUtil.replaceFragment(fragmentManager, Constants.CONTAINER_RES_ID, mPhotoFragment, Constants.PHOTO_FRAGMENT_TAG);
                break;
            case Constants.SHARE_FRAGMENT:
                ShareFragment shareFragment = ShareFragment.newInstance(optionalValues[0]);
                FragmentUtil.addFragment(fragmentManager, Constants.CONTAINER_RES_ID, shareFragment, R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom);
                break;
        }
    }

    @Override public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.PHOTO_FRAGMENT_TAG);
        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (Constants.IS_DEBUG) Log.d(TAG, "popping back stack");
            fragmentManager.popBackStack();
        } else if (fragment instanceof PhotoFragment) {
            if (Constants.IS_DEBUG) Log.d(TAG, "back to camera front fragment");
            switchFragmentTo(Constants.CAMERA_FRONT_FRAGMENT);
        } else {
            if (Constants.IS_DEBUG) Log.d(TAG, "nothing on back stack, calling super");
            super.onBackPressed();
        }
    }
}
