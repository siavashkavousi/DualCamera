package com.siavash.dualcamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Surface on which the camera projects it's capture results. This is derived from Google's docs
 */
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getSimpleName();
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
     * Update the layout based on rotation and orientation changes.
     *
     * @param changed
     */
    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && mCameraLayout.getChildCount() > 0) {
            final View child = mCameraLayout.getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }
            if (previewWidth == 0) {
                previewWidth = 1;
            }
            if (previewHeight == 0) {
                previewHeight = 1;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
            }
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        Point displaySize = new Point();
        Activity activity = (Activity) mContext;
        {
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getSize(displaySize);
            Log.d(TAG, "display size: " + displaySize.x + "," + displaySize.y);
        }
        double targetRatio = (double) displaySize.x / (double) displaySize.y;
        int targetHeight = Math.min(displaySize.y, displaySize.x);
        if (targetHeight <= 0) {
            targetHeight = displaySize.y;
        }
        // Try to find the size which matches the aspect ratio, and is closest match to display height
        for (Camera.Size size : sizes) {
            Log.d(TAG, "supported preview size: " + size.width + "," + size.height);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            // can't find match for aspect ratio, so find closest one
            Log.d(TAG, "no preview size matches the aspect ratio");
            optimalSize = getClosestSize(sizes, targetRatio);
        }
        Log.d(TAG, "chosen optimalSize: " + optimalSize.width + " x " + optimalSize.height);
        Log.d(TAG, "optimal size ratio: " + ((double) optimalSize.width / optimalSize.height));
        return optimalSize;
    }

    private Camera.Size getClosestSize(List<Camera.Size> sizes, double targetRatio) {
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(ratio - targetRatio);
            }
        }
        return optimalSize;
    }

//    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
//        final double ASPECT_TOLERANCE = 0.05;
//        double targetRatio = (double) width / height;
//        if (sizes == null) return null;
//        Camera.Size optimalSize = null;
//        double minDiff = Double.MAX_VALUE;
//        // Try to find an size match aspect ratio and size
//        for (Camera.Size size : sizes) {
//            double ratio = (double) size.width / size.height;
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.height - height) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.height - height);
//            }
//        }
//        // Cannot find the one match the aspect ratio, ignore the requirement
//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Camera.Size size : sizes) {
//                if (Math.abs(size.height - height) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.height - height);
//                }
//            }
//        }
//        return optimalSize;
//    }

    public Camera getCamera() {
        return mCamera;
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }
}

