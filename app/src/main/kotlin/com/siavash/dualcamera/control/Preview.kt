package com.siavash.dualcamera.control

import android.os.Build
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.siavash.dualcamera.utils.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by sia on 12/4/15.
 */
class Preview : SurfaceHolder.Callback {
    private lateinit var cameraController: CameraController
    private lateinit var holder: SurfaceHolder

    constructor(surfaceView: SurfaceView) {
        holder = surfaceView.holder
        holder.addCallback(this)

        val sdk = Build.VERSION.SDK_INT
        //        if (sdk < Build.VERSION_CODES.LOLLIPOP)
        cameraController = CameraController1()
    }

    fun startPreview() {
        cameraController.setPreviewDisplay(holder)
        cameraController.startPreview()
    }

    fun openCamera(cameraId: CameraId) {
        cameraController.open(cameraId)
    }

    fun releaseCamera() {
        cameraController.stopPreview()
        cameraController.release()
    }

    fun takePicture(cameraId: CameraId, atTheBeginning: () -> Unit = {}, inTheEnd: () -> Unit = {}) {
        fun saveTakenPicture(data: ByteArray) {
            executor.execute {
                d("save taken picture with cameraId: " + cameraId)
                val outputStream = FileOutputStream(File(getExternalApplicationStorage(), cameraId.address))
                outputStream.use { outputStream.write(data) }
                cameraPhotoDoneSignal.countDown()
                System.gc()
                d("save taken picture end...")
            }
        }

        cameraController.takePicture { data ->
            d("camera takePicture called")
            atTheBeginning()
            saveTakenPicture(data)
            inTheEnd()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        cameraController.setPreviewDisplay(holder!!)
        cameraController.startPreview()
    }
}