package com.siavash.dualcamera.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.siavash.dualcamera.R
import com.siavash.dualcamera.activities.PhotoActivity
import com.siavash.dualcamera.util.*
import org.jetbrains.anko.*
import java.io.File
import kotlin.concurrent.currentThread

/**
 * Editing photos before saving into file or sharing with others
 * Created by sia on 8/18/15.
 */
class PhotoFragment : Fragment(), AnkoLogger {
    val photoLayout: RelativeLayout by bindView(R.id.photo_layout)
    val backImageView: ImageView by bindView(R.id.photo_back)
    val frontImageView: ImageView by bindView(R.id.photo_front)
    val progressDialog: ProgressDialog by lazy { ProgressDialog.show(act as Context, "در حال بارگذاری", "در حال بارگذاری عکس ها", true, true) }

    var frontBitmap: Bitmap? = null
    var backBitmap: Bitmap? = null
    val callback: OnFragmentInteractionListener by lazy { act as OnFragmentInteractionListener }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo, container, false)
        if (act is PhotoActivity) {
            val pact = act as PhotoActivity
            pact.toolbarTitle.text = "ویرایش عکس"
            pact.toolbarAction.visibility = View.VISIBLE
            pact.toolbarAction.onClick {
                val imageUrl = pact.toolbarAction.saveBitmap(getOutputMediaFile())
                callback.switchFragmentTo(FragmentId.SHARE, imageUrl)
            }
        }
        progressDialog.show()
        loadPhotos()

        return view
    }

    private fun loadPhotos() {
        debug("load photos")
//        async {
        Thread(Runnable {
            debug("thread : " + currentThread)
            val metrics = DisplayMetrics()
            act.windowManager.defaultDisplay.getMetrics(metrics)
            doneSignal.await()
            debug("done signal " + doneSignal.count)
            frontBitmap = Util.decodeSampledBitmap(File(ctx.cacheDir, CameraId.FRONT.address), metrics.widthPixels / 4, metrics.heightPixels / 4)
            backBitmap = Util.decodeSampledBitmap(File(ctx.cacheDir, CameraId.BACK.address), metrics.widthPixels, metrics.heightPixels)
            photoLayout.saveBitmap(File(act.cacheDir, dualPhotoUrl))
            setPhotosToImageView()
        }).start()

//        }
    }

    private fun setPhotosToImageView() {
        frontImageView.post {
            frontImageView.y = 200f
            frontImageView.setImageBitmap(frontBitmap)
            frontImageView.setOnTouchListener(OnTouchListener())
        }
        backImageView.post { backImageView.setImageBitmap(backBitmap) }

        progressDialog.dismiss()
    }

    private inner class OnTouchListener : View.OnTouchListener {
        private var lastEvent: FloatArray? = null
        private var angle = 0f
        private var newRotation = 0f
        private var oldDistance = 1f
        private val matrix: Matrix
        private val savedMatrix: Matrix

        private var mode = OnTouchAction.NONE
        // Remember some things for zooming
        private val startPoint = PointF()
        private val mid = PointF()
        private var dx: Float = 0.toFloat()
        private var dy: Float = 0.toFloat()
        private var dz: Float = 0.toFloat()
        private var dw: Float = 0.toFloat()
        private var x: Float = 0.toFloat()
        private var y: Float = 0.toFloat()
        private val z: Float = 0.toFloat()
        private val w: Float = 0.toFloat()

        init {
            matrix = Matrix()
            savedMatrix = Matrix()
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val view = v as ImageView
            view.scaleType = ImageView.ScaleType.MATRIX

            val layoutParams = view.layoutParams as RelativeLayout.LayoutParams

            // Handle touch events here...
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    view.parent.requestDisallowInterceptTouchEvent(true)

                    dx = event.rawX - layoutParams.leftMargin
                    dy = event.rawY - layoutParams.topMargin
                    dz = event.rawX - layoutParams.bottomMargin
                    dw = event.rawX - layoutParams.rightMargin

                    savedMatrix.set(matrix)
                    startPoint.set(event.x, event.y)
                    mode = OnTouchAction.DRAG
                    lastEvent = null
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDistance = spacing(event).toFloat()
                    if (oldDistance > 10f) {
                        savedMatrix.set(matrix)
                        midPoint(mid, event)
                        mode = OnTouchAction.ZOOM
                    }
                    lastEvent = FloatArray(4)
                    (lastEvent as FloatArray)[0] = event.getX(0)
                    (lastEvent as FloatArray)[1] = event.getX(1)
                    (lastEvent as FloatArray)[2] = event.getY(0)
                    (lastEvent as FloatArray)[3] = event.getY(1)
                    angle = rotate(event)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = OnTouchAction.NONE
                    lastEvent = null
                }
                MotionEvent.ACTION_MOVE -> {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    if (mode == OnTouchAction.DRAG) {
                        matrix.set(savedMatrix)

                        x = event.rawX
                        y = event.rawY

                        layoutParams.leftMargin = (x - dx).toInt()
                        layoutParams.topMargin = (y - dy).toInt()
                        layoutParams.bottomMargin = (z - dz).toInt()
                        layoutParams.rightMargin = (w - dw).toInt()

                        view.layoutParams = layoutParams
                    } else if (mode == OnTouchAction.ZOOM && event.pointerCount == 2) {
                        val newDistance = spacing(event).toFloat()
                        matrix.set(savedMatrix)
                        if (newDistance > 20f) {
                            val scale = newDistance / oldDistance
                            matrix.postScale(scale, scale, mid.x, mid.y)
                        }
                        if (lastEvent != null) {
                            newRotation = rotate(event)
                            val r = newRotation - angle
                            matrix.postRotate(r, (view.measuredWidth / 2).toFloat(), (view.measuredHeight / 2).toFloat())
                        }
                    }
                }
            }

            view.imageMatrix = matrix

            return true
        }

        private fun rotate(event: MotionEvent): Float {
            return Math.toDegrees(Math.atan2((event.getY(0) - event.getY(1)).toDouble(), (event.getX(0) - event.getX(1)).toDouble())).toFloat()
        }

        private fun spacing(event: MotionEvent): Double {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return Math.sqrt((x * x + y * y).toDouble())
        }

        private fun midPoint(point: PointF, event: MotionEvent) {
            val x = event.getX(0) + event.getX(1)
            val y = event.getY(0) + event.getY(1)
            point.set(x / 2, y / 2)
        }
    }
}
