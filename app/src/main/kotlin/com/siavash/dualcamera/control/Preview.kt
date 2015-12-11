package com.siavash.dualcamera.control

import android.os.Build
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.siavash.dualcamera.util.CameraId
import com.siavash.dualcamera.util.cameraPhotoDoneSignal
import com.siavash.dualcamera.util.executor
import com.siavash.dualcamera.util.getExternalApplicationStorage
import java.io.File
import java.io.FileOutputStream

/**
 * Created by sia on 12/4/15.
 */
class Preview : SurfaceHolder.Callback {
    private lateinit var cameraController: CameraController
    private lateinit var holder: SurfaceHolder
    lateinit var cameraId : CameraId

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
        this.cameraId = cameraId
        cameraController.open(cameraId)
    }

    fun releaseCamera() {
        cameraController.stopPreview()
        cameraController.release()
    }

    fun takePicture(atTheBeginning: () -> Unit = {}, inTheEnd: () -> Unit = {}) {
        cameraController.takePicture { data ->
            atTheBeginning()
            saveTakenPicture(data)
            inTheEnd()
        }
    }

    private fun saveTakenPicture(data: ByteArray) {
        executor.execute {
            Log.d("Preview", "thread " + Thread.currentThread())
            val outputStream = FileOutputStream(File(getExternalApplicationStorage(), cameraId.address))
            outputStream.use { outputStream.write(data) }
            cameraPhotoDoneSignal.countDown()
            System.gc()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        cameraController.stopPreview()
        cameraController.release()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        cameraController.setPreviewDisplay(holder!!)
        cameraController.startPreview()
    }
}