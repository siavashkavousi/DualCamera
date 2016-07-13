package com.siavash.dualcamera.control

import android.view.SurfaceHolder
import com.siavash.dualcamera.utils.CameraId

/**
 * Created by sia on 12/4/15.
 */
abstract class CameraController() {
    abstract fun open(cameraId: CameraId)
    abstract fun setPreviewDisplay(holder: SurfaceHolder)
    abstract fun startPreview()
    abstract fun stopPreview()
    abstract fun takePicture(function: (ByteArray) -> Unit)
    abstract fun unlock()
    abstract fun release()
    abstract fun reconnect()
}