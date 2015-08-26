package com.siavash.dualcamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.Log;

import com.siavash.dualcamera.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility for saving, loading, rotating, resizing and ... of bitmaps
 * Created by sia on 8/18/15.
 */
public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    public static void save(Context context, byte[] data, String url, int orientation){
        long totalTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        File file = new File(context.getCacheDir(), url);
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

    @Nullable public static byte[] load(Context context, String url) {
        File file = new File(context.getCacheDir(), url);
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

    @Nullable public static Bitmap decodeBitmap(Context context, String url, BitmapFactory.Options options){
        File file = new File(context.getCacheDir(), url);
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable public static Bitmap decodeSampledBitmap(Context context, String url, int reqWidth, int reqHeight) {
        long time = System.currentTimeMillis();
        // first decode check the raw image dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeBitmap(context, url, options);

        // calculate the factor to scale down by depending on the desired height
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inScaled = false;
        // decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // measure the complexity time of decoding bitmap
        time = System.currentTimeMillis() - time;
        Log.i(TAG, "complexity time of decoding bitmap is: " + time);

        return decodeBitmap(context, url, options);
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
