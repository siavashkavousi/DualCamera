package com.siavash.dualcamera.control

import android.hardware.Camera
import android.view.SurfaceHolder
import com.siavash.dualcamera.util.CameraId
import com.siavash.dualcamera.util.minElementIndex

/**
 * Created by sia on 12/4/15.
 */
@Suppress("deprecation")
class CameraController1() : CameraController() {
    private var camera: Camera? = null

    override fun open(cameraId: CameraId) {
        if (camera == null) {
            camera = Camera.open(cameraId.id)
            camera?.setUp()
        }
    }

    override fun setPreviewDisplay(holder: SurfaceHolder) {
        camera?.setPreviewDisplay(holder)
    }

    override fun startPreview() {
        camera?.startPreview()
    }

    override fun stopPreview() {
        camera?.stopPreview()
    }

    override fun takePicture(function: (ByteArray) -> Unit) {
        camera?.takePicture(null, null, { data, camera ->
            function(data)
        })
    }

    override fun unlock() {
        stopPreview()
        camera?.unlock()
    }

    override fun release() {
        camera?.release()
        camera = null
    }

    override fun reconnect() {
        camera?.reconnect()
    }

    private fun Camera.setUp() {
        val preferredPreviewSize = parameters.getPreferredPreviewSize()
        parameters.setPreviewSize(preferredPreviewSize.width, preferredPreviewSize.height)
        if (parameters.supportedFlashModes != null) parameters.supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)
        setDisplayOrientation(90)
    }

    private fun Camera.Parameters.getPreferredPreviewSize(): Camera.Size {
        return supportedPreviewSizes.getPreferredSize()
    }

    private fun List<Camera.Size>.getPreferredSize(): Camera.Size {
        val ratios = FloatArray(this.size)
        for (i in this.indices) {
            val width = this[i].width.toFloat()
            val height = this[i].height.toFloat()
            ratios[i] = if (width > height) width / height else height / width
        }
        return this[ratios.minElementIndex()!!]
    }
}