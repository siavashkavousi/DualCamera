package com.siavash.dualcamera.activities

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import android.widget.ImageButton
import com.siavash.dualcamera.R
import com.siavash.dualcamera.control.Preview
import com.siavash.dualcamera.util.*
import org.jetbrains.anko.act
import org.jetbrains.anko.startActivity

class CameraActivity : AppCompatActivity() {
    private val frontView: SurfaceView by bindView(R.id.front_container)
    private val backView: SurfaceView by bindView(R.id.back_container)
    private val shutter: ImageButton by bindView(R.id.shutter_btn)

    private var cameraId: CameraId = CameraId.FRONT
    private lateinit var backPreview: Preview
    private lateinit var frontPreview: Preview
    private lateinit var displaySize: Point

    override fun onResume() {
        super.onResume()
        cameraPhotoDoneSignal.reset()
        cameraId = CameraId.FRONT
        safeOpenCameraAndPreview(cameraId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frontPreview = Preview(CameraId.FRONT, frontView)
        backPreview = Preview(CameraId.BACK, backView)

        shutter.setOnClickListener { takePicture() }
        displaySize = getDisplaySize(act)
    }

    private fun safeOpenCameraAndPreview(cameraId: CameraId) {
        fun frontCameraOpenAndPreview() {
            frontPreview.openCamera()
            frontPreview.startPreview()
        }

        fun backCameraOpenAndPreview() {
            backPreview.openCamera()
            backPreview.startPreview()
        }
        executor.execute {
            releaseCameraResourcesIfAvailable()
            if (cameraId == CameraId.FRONT) {
                try {
                    frontCameraOpenAndPreview()
                } catch(e: RuntimeException) {
                    releaseCameraResourcesIfAvailable()
                    frontCameraOpenAndPreview()
                }
            } else {
                try {
                    backCameraOpenAndPreview()
                } catch(e: RuntimeException) {
                    releaseCameraResourcesIfAvailable()
                    backCameraOpenAndPreview()
                }
            }
        }
    }

    private fun releaseCameraResourcesIfAvailable() {
        frontPreview.releaseCamera()
        backPreview.releaseCamera()
    }

    private fun switchCamera() {
        if (cameraId == CameraId.FRONT) {
            cameraId = CameraId.BACK
            safeOpenCameraAndPreview(cameraId)
        } else {
            cameraId = CameraId.FRONT
            safeOpenCameraAndPreview(cameraId)
        }
    }

    private fun takePicture() {
        shutter.isClickable = false
        if (cameraId == CameraId.FRONT) {
            frontPreview.takePicture({ switchCamera() }, { shutter.isClickable = true })
        } else {
            backPreview.takePicture({ releaseCameraResourcesIfAvailable() }, { shutter.isClickable = true })
            startActivity<PhotoActivity>()
        }
    }
}
