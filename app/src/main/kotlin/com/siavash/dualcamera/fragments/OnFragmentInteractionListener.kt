package com.siavash.dualcamera.fragments

import com.siavash.dualcamera.util.FragmentId

/**
 * Created by sia on 10/25/15.
 */
interface OnFragmentInteractionListener {
    fun switchFragmentTo(fragmentId: FragmentId, vararg optionalValues: String)
}
