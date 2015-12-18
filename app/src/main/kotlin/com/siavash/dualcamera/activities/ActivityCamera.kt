package com.siavash.dualcamera.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import android.widget.ImageButton
import com.siavash.dualcamera.R
import com.siavash.dualcamera.control.Preview
import com.siavash.dualcamera.util.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class ActivityCamera : AppCompatActivity() {
    private val container: SurfaceView by bindView(R.id.container)
    private val shutter: ImageButton by bindView(R.id.shutter_btn)

    private var cameraId: CameraId = CameraId.FRONT
    private lateinit var preview: Preview

    override fun onResume() {
        super.onResume()
        cameraPhotoDoneSignal.reset()
        cameraId = CameraId.FRONT
        safeOpenCameraAndPreview(cameraId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preview = Preview(container)

        shutter.setOnClickListener { takePicture() }
    }

    private fun safeOpenCameraAndPreview(cameraId: CameraId) {
        fun cameraOpenAndPreview() {
            preview.openCamera(cameraId)
            preview.startPreview()
        }

        executor.execute {
            preview.releaseCamera()
            try {
                cameraOpenAndPreview()
            } catch(e: RuntimeException) {
                preview.releaseCamera()
                cameraOpenAndPreview()
            }
        }
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
            preview.takePicture({ switchCamera() }, { shutter.isClickable = true })
        } else {
            preview.takePicture({ preview.releaseCamera() }, { shutter.isClickable = true })
            startActivity<ActivityPhoto>()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        preview.releaseCamera()
        sendIntentForCommentInCafeBazaar(ctx)
    }
}
