package com.siavash.dualcamera;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Surface on which the camera projects it's capture results. This is derived from Google's docs
 */
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    // SurfaceHolder
    private SurfaceHolder mHolder;
    // Our Camera.
    private Camera mCamera;
    // Parent Context.
    private Context mContext;
    // Camera Sizing (For rotation, orientation changes)
    private Camera.Size mPreviewSize;
    // List of supported preview sizes
    private List<Camera.Size> mSupportedPreviewSizes;
    // Flash modes supported by this camera
    private List<String> mSupportedFlashModes;
    // View holding this camera.
    private ViewGroup mCameraLayout;

    public CameraPreview(Context context, Camera camera, ViewGroup cameraLayout) {
        super(context);

        // Capture the context
        mCameraLayout = cameraLayout;
        mContext = context;
        setUpCamera(camera);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
    }

    /**
     * Begin the preview of the camera input.
     */
    public void startCameraPreview() {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(Constants.DISPLAY_ORIENTATION);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract supported preview and flash modes from the camera.
     *
     * @param camera
     */
    private void setUpCamera(Camera camera) {
        mCamera = camera;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();

        // Set the camera to Auto Flash mode.
        if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(parameters);
        }

        requestLayout();
    }

    /**
     * The Surface has been created, now tell the camera where to draw the preview.
     *
     * @param holder
     */
    @Override public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dispose of the camera preview.
     *
     * @param holder
     */
    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /**
     * React to surface changed events
     *
     * @param holder
     * @param format
     * @param w
     * @param h
     */
    @Override public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            // Set the auto-focus mode to "continuous"
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            // Preview size must exist.
            if (mPreviewSize != null) {
                Camera.Size previewSize = mPreviewSize;
                parameters.setPreviewSize(previewSize.width, previewSize.height);
            }

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the measurements of the layout
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null){
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    /**
     * Update the layout based on rotation and orientation changes.
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;

            if (mPreviewSize != null) {
                Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation()) {
                    case Surface.ROTATION_0:
                        previewWidth = mPreviewSize.height;
                        previewHeight = mPreviewSize.width;
                        /// FIXME: 8/18/15
                        mCamera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_90:
                        previewWidth = mPreviewSize.width;
                        previewHeight = mPreviewSize.height;
                        break;
                    case Surface.ROTATION_180:
                        previewWidth = mPreviewSize.height;
                        previewHeight = mPreviewSize.width;
                        break;
                    case Surface.ROTATION_270:
                        previewWidth = mPreviewSize.width;
                        previewHeight = mPreviewSize.height;
                        //// FIXME: 8/18/15
                        mCamera.setDisplayOrientation(180);
                        break;
                }
            }

            final int scaledChildHeight = previewHeight * width / previewWidth;
            mCameraLayout.layout(0, height - scaledChildHeight, width, height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match which suits the whole screen minus the menu on the left.
        for (Camera.Size size : sizes){

            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE){
                optimalSize = size;
            }
        }

        // If we cannot find the one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null) {
            // TODO : Backup in case we don't get a size.
        }

        return optimalSize;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }
}

