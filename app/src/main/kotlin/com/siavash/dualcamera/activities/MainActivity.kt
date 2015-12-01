package com.siavash.dualcamera.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.siavash.dualcamera.R
import com.siavash.dualcamera.fragments.CameraFragment
import com.siavash.dualcamera.fragments.OnFragmentInteractionListener
import com.siavash.dualcamera.util.CameraId
import com.siavash.dualcamera.util.FragmentId
import com.siavash.dualcamera.util.cameraPhotoDoneSignal
import com.siavash.dualcamera.util.replaceFragment
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {

    override fun onResume() {
        super.onResume()
        cameraPhotoDoneSignal.reset()
        switchFragmentTo(FragmentId.CAMERA_FRONT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun switchFragmentTo(fragmentId: FragmentId, vararg optionalValues: String) {
        if (fragmentId == FragmentId.CAMERA_FRONT) fragmentManager.replaceFragment(R.id.container, CameraFragment(CameraId.FRONT, FragmentId.CAMERA_BACK))
        else if (fragmentId == FragmentId.CAMERA_BACK) fragmentManager.replaceFragment(R.id.container, CameraFragment(CameraId.BACK, FragmentId.PHOTO))
        else if (fragmentId == FragmentId.PHOTO) startActivity<PhotoActivity>()
    }
}
