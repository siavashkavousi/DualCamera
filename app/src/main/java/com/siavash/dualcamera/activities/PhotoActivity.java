package com.siavash.dualcamera.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.R;
import com.siavash.dualcamera.fragments.CameraBase;
import com.siavash.dualcamera.fragments.CameraFront;
import com.siavash.dualcamera.fragments.PhotoFragment;
import com.siavash.dualcamera.util.FragmentUtil;

import butterknife.ButterKnife;

/**
 * Created by sia on 10/21/15.
 */
public class PhotoActivity extends AppCompatActivity {
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
}
