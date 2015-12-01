//package com.siavash.dualcamera.control
//
//import android.content.Context
//import android.hardware.Camera
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.view.ViewGroup
//
///**
// * Created by sia on 11/18/15.
// */
//class KCameraPreview(val parentContext: Context, var camera: Camera?, cameraLayout: ViewGroup) : SurfaceView(parentContext), SurfaceHolder.Callback {
//    private val surfaceHolder: SurfaceHolder by lazy { holder }
//    private var supportedFlashModes = camera?.parameters.supportedFlashModes
//
//    fun setUpFlashModes() {
//        if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
//            val parameters = camera?.parameters
//            parameters?.flashMode = Camera.Parameters.FLASH_MODE_AUTO
//            camera?.parameters = parameters
//        }
//        requestLayout()
//    }
//
//    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder?) {
//        camera?.stopPreview()
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder?) {
//        camera?.setPreviewDisplay(holder)
//    }
//
//    //    /**
//    //     * Dispose of the camera preview.
//    //     *
//    //     * @param holder
//    //     */
//    //    @Override public void surfaceDestroyed(SurfaceHolder holder) {
//    //        if (mCamera != null) {
//    //            mCamera.stopPreview();
//    //        }
//    //    }
//    //
//    //    /**
//    //     * React to surface changed events
//    //     *
//    //     * @param holder
//    //     * @param format
//    //     * @param w
//    //     * @param h
//    //     */
//    //    @Override public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//    //        // If your preview can change or rotate, take care of those events here.
//    //        // Make sure to stop the preview before resizing or reformatting it.
//    //
//    //        if (mHolder.getSurface() == null) {
//    //            // preview surface does not exist
//    //            return;
//    //        }
//    //
//    //        // stop preview before making changes
//    //        try {
//    //            Camera.Parameters parameters = mCamera.getParameters();
//    //
//    //            // Set the auto-focus mode to "continuous"
//    //            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//    //
//    //            // Preview size must exist.
//    //            if (mPreviewSize != null) {
//    //                Camera.Size previewSize = mPreviewSize;
//    //                parameters.setPreviewSize(previewSize.width, previewSize.height);
//    //            }
//    //
//    //            mCamera.setParameters(parameters);
//    //            mCamera.startPreview();
//    //        } catch (Exception e) {
//    //            e.printStackTrace();
//    //        }
//    //    }
//
//}