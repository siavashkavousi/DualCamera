package com.siavash.dualcamera.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.siavash.dualcamera.ApplicationBase;
import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for saving, loading, rotating, resizing and ... of bitmaps
 * Created by sia on 8/18/15.
 */
public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    public static void save(Context context, Bitmap bitmap){
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null){
            Toast.makeText(context, context.getResources().getString(R.string.save_photo_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos);
        bitmap.recycle();
    }

    /**
     * method to generate a unique name for output file
     * @return camera output file
     */
    @Nullable private static File getOutputMediaFile(Context context) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.app_name));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        Toast.makeText(context, context.getResources().getString(R.string.save_photo), Toast.LENGTH_LONG).show();

        return mediaFile;
    }


    public static void save(byte[] data, String url, int orientation){
        long totalTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        File file = new File(ApplicationBase.getAppContext().getCacheDir(), url);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Log.d(TAG, "decodeByteArray: " + String.valueOf(System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        if (orientation != 0) {
            Matrix matrix = new Matrix();
            if (bitmap.getWidth() > bitmap.getHeight()) {
                matrix.postRotate(orientation);
            } else {
                matrix.postRotate(-orientation);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Log.d(TAG, "createBitmap: " + String.valueOf(System.currentTimeMillis() - time));
        }
        time = System.currentTimeMillis();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos);
        Log.d(TAG, "compress: " + String.valueOf(System.currentTimeMillis() - time));

        bitmap.recycle();

        Log.d(TAG, "save bitmap with orientation: " + String.valueOf(System.currentTimeMillis() - totalTime));
    }

    @Nullable public static byte[] load(String url) {
        File file = new File(ApplicationBase.getAppContext().getCacheDir(), url);
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byte[] bytes = new byte[2048];
            int bytesRead;
            while ((bytesRead = fis.read(bytes)) != -1) {
                byteArray.write(bytes, 0, bytesRead);
            }
            return byteArray.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable public static Bitmap decodeBitmap(String url, BitmapFactory.Options options){
        File file = new File(ApplicationBase.getAppContext().getCacheDir(), url);
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable public static Bitmap decodeSampledBitmap(String url, int reqWidth, int reqHeight) {
        long time = System.currentTimeMillis();
        // first decode check the raw image dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeBitmap(url, options);

        // calculate the factor to scale down by depending on the desired height
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inScaled = false;
        // decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // measure the complexity time of decoding bitmap
        time = System.currentTimeMillis() - time;
        Log.i(TAG, "complexity time of decoding bitmap is: " + time);

        return decodeBitmap(url, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // raw height and width of image
        final int imageHeight = options.outHeight;
        final int imageWidth = options.outWidth;

        // calculate the factor to scale down by depending on the desired height
        int inSampleSize = 1;
        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            final int heightRatio = imageHeight / reqHeight;
            final int widthRatio = imageWidth / reqWidth;

            // choose the smallest factor to scale down by, so the scaled image is always slightly larger than needed
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        Log.i(TAG, "calculateInSampleSize: imageHeight = " + imageHeight + " imageWidth = " + imageWidth
                + " reqWidth = " + reqWidth + " reqHeight = " + reqHeight
                + " inSampleSize = " + inSampleSize);

        return inSampleSize;
    }
}
