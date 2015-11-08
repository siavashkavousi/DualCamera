package com.siavash.dualcamera.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.siavash.dualcamera.R
import com.siavash.dualcamera.fragments.CameraFragment
import com.siavash.dualcamera.fragments.OnFragmentInteractionListener
import com.siavash.dualcamera.util.CameraId
import com.siavash.dualcamera.util.FragmentId
import com.siavash.dualcamera.util.countDownLatch
import com.siavash.dualcamera.util.replaceFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener, AnkoLogger {

    override fun onResume() {
        super.onResume()
        countDownLatch.reset()
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

    //    override fun onBackPressed() {
    //        val fragmentManager = fragmentManager
    //        val fragment = fragmentManager.findFragmentByTag(Constants.PHOTO_FRAGMENT_TAG)
    //        if (fragmentManager.backStackEntryCount > 0) {
    //            if (Constants.IS_DEBUG) Timber.d("popping back stack")
    //            fragmentManager.popBackStack()
    //        } else if (fragment is PhotoFragment) {
    //            if (Constants.IS_DEBUG) Timber.d("back to camera front fragment")
    //            switchFragmentTo(FragmentId.PHOTO)
    //        } else {
    //            if (Constants.IS_DEBUG) Timber.d("nothing on back stack, calling super")
    //            super.onBackPressed()
    //        }
    //    }
}
