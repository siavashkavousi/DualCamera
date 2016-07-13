package com.siavash.dualcamera.control

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.siavash.dualcamera.utils.*
import org.jetbrains.anko.custom.onUiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by sia on 12/4/15.
 */
class Preview : SurfaceHolder.Callback {
    private lateinit var cameraController: CameraController
    private lateinit var holder: SurfaceHolder
    private lateinit var act: Activity

    constructor(act: Activity, surfaceView: SurfaceView) {
        this.act = act
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
        fun showAlertDialog() {
            fun createAlertDialog(): AlertDialog {
                return AlertDialog.Builder(act)
                        .setTitle("خطا")
                        .setMessage("حافظه گوشی پر شده است")
                        .setPositiveButton("باشه", { dialogInterface, i -> act.finish() })
                        .setCancelable(false)
                        .create()
            }

            act.onUiThread { createAlertDialog().show() }
        }

        fun saveTakenPicture(data: ByteArray) {
            executor.execute {
                d("save taken picture with cameraId: " + cameraId)
                val outputStream = FileOutputStream(File(getExternalApplicationStorage(), cameraId.address))
                outputStream.use {
                    try {
                        outputStream.write(data)
                    } catch(e: IOException) {
                        showAlertDialog()
                    }
                }
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