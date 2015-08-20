package com.siavash.dualcamera;

import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by sia on 8/14/15.
 */
public abstract class CameraBase {
    private static final String TAG = CameraBase.class.getSimpleName();
    // Native camera.
    protected Camera mCamera;
    // View to display the camera output.
    protected CameraPreview mPreview;
    // view's context
    protected Context mContext;
    protected CameraView mCameraView;
    // camera picture callback
    protected PictureCallback mPictureCallback;
    // picture url
    private String mUrl;

    public CameraBase(CameraView cameraView, Context context) {
        Log.d(TAG, "CameraBase is created");
        mCameraView = cameraView;
        mContext = context;
        mPictureCallback = new PictureCallback();
    }

    /**
     * Safe method for getting a camera instance.
     *
     * @param id camera id backCamera = 0, frontCamera = 1
     * @return camera instance
     */
    protected static Camera getCameraInstance(int id) {
        Camera c = null;
        try {
            c = Camera.open(id); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Recommended "safe" way to open the camera.
     *
     * @param viewLayout
     * @return
     */
    protected abstract boolean safeCameraOpenInView(ViewGroup viewLayout);

    /**
     * Clear any existing preview / camera.
     */
    protected void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.setCamera(null);
        }
    }

    public void takePicture(String url) {
        mUrl = url;
        mCamera.takePicture(null, null, mPictureCallback);
    }

    private class PictureCallback implements Camera.PictureCallback {
        private OnPhotoSaved onPhotoSaved;

        public PictureCallback() {
            onPhotoSaved = mCameraView;
        }

        @Override public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken called! saving into file is about to start");
            if (mUrl.isEmpty()) return;
            IO.save(mContext, data, mUrl);

            releaseCameraAndPreview();
            onPhotoSaved.onPhotoSavedComplete();
        }

        /**
         * Used to return the camera File output.
         *
         * @return
         */
        private File getOutputMediaFile() {

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "DualCamera");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "Required media storage does not exist");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");

            Toast.makeText(mContext, "Your picture has been saved!", Toast.LENGTH_LONG).show();

            return mediaFile;
        }

        private File getTempFile(Context context, String url) {
            File file = null;
            try {
                String fileName = Uri.parse(url).getLastPathSegment();
                file = File.createTempFile(fileName, null, context.getCacheDir());
                Log.d(TAG, "file name : " + fileName + " and file : " + file);
            } catch (IOException e) {
                // Error while creating file
            }
            return file;
        }

        private void asyncSavePhoto(final byte[] data) {
            Observable.just(new Action1<Void>() {
                @Override public void call(Void aVoid) {
                    if (mUrl.isEmpty()) return;
//                    File pictureFile = getTempFile(mContext, mUrl);
                    File pictureFile = getOutputMediaFile();
                    if (pictureFile == null) {
                        Toast.makeText(mContext, "Image retrieval failed.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(data);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).subscribeOn(Schedulers.newThread());
        }
    }
}
