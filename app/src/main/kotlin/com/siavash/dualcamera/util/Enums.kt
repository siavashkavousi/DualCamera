package com.siavash.dualcamera.util

/**
 * Created by sia on 10/25/15.
 */
enum class FragmentId {
    CAMERA_BACK, CAMERA_FRONT, PHOTO, SHARE, ABOUT
}

enum class CameraId(val address: String, val id: Int) {
    BACK(".back", 0), FRONT(".front", 1)
}

enum class OnTouchAction {
    NONE, DRAG, ZOOM
}