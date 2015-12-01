package com.siavash.dualcamera.fragments

import android.app.Fragment
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.siavash.dualcamera.Constants
import com.siavash.dualcamera.R
import com.siavash.dualcamera.control.CameraPreview
import com.siavash.dualcamera.util.*
import org.jetbrains.anko.act
import org.jetbrains.anko.info
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.currentThread

/**
 * Parent class for camera controllers
 * Created by sia on 8/14/15.
 */
@Suppress("deprecation")
class CameraFragment(val cameraId: CameraId, val nextFragmentId: FragmentId) : Fragment() {
    var camera: Camera? = null
    var preview: CameraPreview? = null
    val frameLayout: FrameLayout by bindView(R.id.container)
    val shutter: ImageButton by bindView(R.id.shutter_btn)
    lateinit var displaySize: Point
    val callback: OnFragmentInteractionListener by lazy { act as OnFragmentInteractionListener }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        safeCameraOpenInView()
        shutter.setOnClickListener { takePicture() }
        displaySize = getDisplaySize(act)
    }

    private fun safeCameraOpenInView(): Boolean {
        val qOpened: Boolean
        releaseCameraAndPreview()
        camera = getCameraInstance(cameraId.id)
        qOpened = (camera != null)

        if (qOpened) {
            preview = CameraPreview(activity, camera, frameLayout)
            frameLayout.addView(preview)
            preview?.startCameraPreview()
        }
        return qOpened
    }

    private fun getCameraInstance(id: Int): Camera? {
        return Camera.open(id)
    }

    private fun releaseCameraAndPreview() {
        camera?.stopPreview()
        camera?.release()
        camera = null
        preview?.destroyDrawingCache()
        preview?.camera = null
    }

    private fun takePicture() {
        camera?.takePicture(null, null, Camera.PictureCallback() { data, camera ->
            executor.execute {
                saveBitmap(data, cameraId)
                cameraPhotoDoneSignal.countDown()
            }
            callback.switchFragmentTo(nextFragmentId)
        })
    }

    private fun saveBitmap(data: ByteArray, cameraId: CameraId, orientation: Int = 90) {
        val fos = FileOutputStream(File(getExternalApplicationStorage(), cameraId.address))
        var bitmap = decodeBitmapOnCameraId(data, cameraId)

        val matrix = Matrix()
        if (isOrientationChangeNeeded(orientation)) changeOrientation(bitmap, matrix, cameraId, orientation)

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos)
        bitmap.recycle()
    }

    private fun decodeBitmapOnCameraId(data: ByteArray, cameraId: CameraId): Bitmap {
        var bitmap: Bitmap
        if (cameraId == CameraId.FRONT) {
            bitmap = decodeSampledBitmap(data, displaySize.x / 4, displaySize.y / 4)
        } else {
            bitmap = decodeSampledBitmap(data, displaySize.x, displaySize.y)
        }
        return bitmap
    }

    private fun isOrientationChangeNeeded(orientation: Int): Boolean {
        return orientation != 0
    }

    private fun changeOrientation(bitmap: Bitmap, matrix: Matrix, cameraId: CameraId, orientation: Int) {
        if (cameraId == CameraId.FRONT) {
            if (bitmap.width < bitmap.height) {
                matrix.postRotate(orientation.toFloat())
            } else {
                matrix.postRotate((-orientation).toFloat())
            }
        } else {
            if (bitmap.width > bitmap.height) {
                matrix.postRotate(orientation.toFloat())
            } else {
                matrix.postRotate((-orientation).toFloat())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        releaseCameraAndPreview()
    }
}
