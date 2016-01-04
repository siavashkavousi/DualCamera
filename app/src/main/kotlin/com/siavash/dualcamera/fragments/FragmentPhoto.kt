package com.siavash.dualcamera.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.siavash.dualcamera.R
import com.siavash.dualcamera.activities.ActivityPhoto
import com.siavash.dualcamera.adapters.AdapterTransformation
import com.siavash.dualcamera.utils.*
import org.jetbrains.anko.act
import org.jetbrains.anko.ctx
import org.jetbrains.anko.onUiThread
import org.jetbrains.anko.toast
import java.io.File

/**
 * Editing photos before saving into file or sharing with others
 * Created by sia on 8/18/15.
 */
class FragmentPhoto : Fragment(), SimpleSwipeGestureListener, OnTransformationTypeListener {
    private val photoLayout: RelativeLayout by bindView(R.id.photo_layout)
    private val backImageView: ImageView by bindView(R.id.photo_back)
    private val frontImageView: ImageView by bindView(R.id.photo_front)
    private val transformationList: RecyclerView by bindView(R.id.transformation_list)

    private val gestureDetector: GestureDetector by lazy { GestureDetector(ctx, SwipeGestureListener(this)) }
    private val progressDialog: ProgressDialog by lazy { ProgressDialog.show(act as Context, "در حال بارگذاری", "در حال بارگذاری عکس ها", true, true) }
    private val transformationDataSet: MutableList<TransformationType> = arrayListOf()
    private val transformationAdapter: AdapterTransformation by lazy { AdapterTransformation(this, transformationDataSet) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo, container, false)
        if (act is ActivityPhoto) setUpToolbar()
        view.setOnTouchListener { view, event -> gestureDetector.onTouchEvent(event) }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressDialog.show()
        progressDialog.setCancelable(false)

        waitForComputationThreads()
        setUpTransformationList()
    }

    private fun setUpToolbar() {
        val toolbar = (act as ActivityPhoto).toolbar
        toolbar.setTitle("ویرایش عکس")
        toolbar.setLeftItemVisibility(View.VISIBLE)
        toolbar.setLeftAction {
            saveBitmap(getOutputMediaFilePath())
        }
    }

    private fun waitForComputationThreads() {
        executor.execute {
            cameraPhotoDoneSignal.await()
            cameraPhotoDoneSignal.reset()
            d("waitForComputation is finished")
            onUiThread { loadBitmaps() }
        }
    }

    private fun loadBitmaps() {
        Glide.with(ctx).load(frontImagePath)
                .bitmapTransform(RotateTransformation(ctx, -90))
                .crossFade()
                .into(frontImageView)

        Glide.with(ctx).load(backImagePath)
                .bitmapTransform(RotateTransformation(ctx, 90))
                .crossFade()
                .into(backImageView)

        frontImageView.setOnTouchListener(OnTouchListener())
        progressDialog.dismiss()
        System.gc()
    }

    private fun setUpTransformationList() {
        transformationList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        loadTransformationData()
        transformationList.adapter = transformationAdapter
        transformationList.addItemDecoration(SpacesItemDecoration(4))
        transformationList.setHasFixedSize(true)
    }

    private fun loadTransformationData() {
        transformationDataSet.addAll(TransformationType.values())
    }

    override fun onSwipe(dir: Swipe) {
        when (dir) {
            Swipe.Up -> showTransformationList()
            Swipe.Down -> hideTransformationList()
        }
    }

    private fun showTransformationList() {
        transformationList.visibility = View.VISIBLE
        val v = ValueAnimator.ofInt(0, dip2px(ctx, 120f))
        v.setDuration(shortAnimTime)
        v.interpolator = AccelerateInterpolator()
        v.addUpdateListener { v ->
            transformationList.layoutParams.height = v.animatedValue as Int
            transformationList.requestLayout()
        }
        v.start()
    }

    private fun hideTransformationList() {
        val v = ValueAnimator.ofInt(dip2px(ctx, 120f), 0)
        v.setDuration(shortAnimTime)
        v.interpolator = DecelerateInterpolator()
        v.addUpdateListener {
            transformationList.layoutParams.height = v.animatedValue as Int
            transformationList.requestLayout()
        }
        v.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                transformationList.visibility = View.INVISIBLE
            }
        })
        v.start()
    }

    /**
     * save bitmap which is hidden with finalImageUrl name
     */
    fun saveBitmap(address: String) {
        if (photoLayout.saveAsBitmap(File(address)) != null)
            toast("عکس شما ذخیره شد")
        else
            toast("دوباره امتحان کنید")
    }

    override fun onTypeSelected(transformationType: TransformationType) {
        frontImageView.setImageWithTransformation(frontImagePath, transformationType)
    }

    private inner class OnTouchListener : View.OnTouchListener {
        private var dx: Float = 0f
        private var dy: Float = 0f
        private var dz: Float = 0f
        private var dw: Float = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            drag(v, event)
            view.invalidate()
            return true
        }

        private fun drag(v: View, event: MotionEvent) {
            val params = frontImageView.layoutParams as RelativeLayout.LayoutParams

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dx = event.rawX - params.leftMargin
                    dy = event.rawY - params.topMargin
                    dw = event.rawX - params.rightMargin
                    dz = event.rawX - params.bottomMargin
                }
                MotionEvent.ACTION_MOVE -> {
                    params.leftMargin = (event.rawX - dx).toInt()
                    params.topMargin = (event.rawY - dy).toInt()
                    params.rightMargin = -dw.toInt()
                    params.bottomMargin = -dz.toInt()
                    v.layoutParams = params
                }
            }
        }
    }

    private inner class SwipeGestureListener(val listener: SimpleSwipeGestureListener) : GestureDetector.SimpleOnGestureListener() {
        private val vc = ViewConfiguration.get(ctx)
        private val swipeMinVelocityY = vc.scaledMinimumFlingVelocity
        private val swipeMinDistanceY = vc.scaledOverflingDistance

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val distanceY = Math.abs(e1.y - e2.y)

            if (Math.abs(velocityY) > swipeMinVelocityY && distanceY > swipeMinDistanceY) {
                if (e1.y > e2.y) listener.onSwipe(Swipe.Up) else listener.onSwipe(Swipe.Down)
                return true
            }

            return false
        }
    }
}
