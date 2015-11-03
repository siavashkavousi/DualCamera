package com.siavash.dualcamera.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.siavash.dualcamera.ApplicationBase;
import com.siavash.dualcamera.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for saving, loading, rotating, resizing and ... of bitmaps
 * Created by sia on 8/18/15.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static <T> T checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    @NonNull public static File getFile(String url) {
        File file = new File(url);
        if (!file.exists())
            throw new NullPointerException("Requested file does not exist in the underlying system");
        return file;
    }

    @NonNull public static File getCacheFile(Context context, String url) {
        File file = new File(context.getCacheDir(), url);
        if (!file.exists())
            throw new NullPointerException("Requested file does not exist in the underlying system");
        return file;
    }

    @Nullable public static File setImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = "IMG_" + timeStamp + ".jpg";
        return setFile(name);
    }

    @Nullable public static File setFile(String name) {
        File imageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), ApplicationBase.getAppName());

        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                if (Constants.IS_DEBUG) Log.d(TAG, "Required media storage does not exist");
                return null;
            }
        }

        return new File(imageDir.getPath() + File.separator + name + ".jpg");
    }

    public static String save(View view, File file) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        String imageUrl = Util.save(bitmap, file);
        view.setDrawingCacheEnabled(false);
        return imageUrl;
    }

    /**
     * Saves bitmap into storage
     *
     * @param bitmap     target bitmap
     * @param targetFile target file in order to save bitmap into it
     * @return absolute path to the saved bitmap
     */
    @Nullable public static String save(Bitmap bitmap, File targetFile) {
        if (targetFile == null) {
            return null;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos);
        return targetFile.getAbsolutePath();
    }

    /**
     * save photo in cache folder of app asynchronously
     *
     * @param context     context need to access cache folder
     * @param data        to be saved
     * @param url         place to save in cache folder
     * @param orientation orientation of the taken photo
     */
    public static void save(final Context context, final byte[] data, final int frontBack, String url, final int orientation) {
        long time = System.currentTimeMillis();
        File file = new File(context.getCacheDir(), url);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DisplayMetrics metrics = getDisplaySize((Activity) context);
        Bitmap bitmap;
        if (frontBack == Constants.CAMERA_BACK_FRAGMENT) {
            bitmap = decodeSampledBitmap(data, metrics.widthPixels / 4, metrics.heightPixels / 4);
        } else {
            bitmap = decodeSampledBitmap(data, metrics.widthPixels, metrics.heightPixels);
        }

        Matrix matrix = new Matrix();
        if (isOrientationChangeNeeded(orientation))
            changeOrientation(bitmap, matrix, frontBack, orientation);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos);
        bitmap.recycle();
        if (Constants.IS_DEBUG)
            Log.d(TAG, "save bitmap with orientation: " + String.valueOf(System.currentTimeMillis() - time) + " - in the thread: " + Thread.currentThread().toString());
    }

    public static boolean isOrientationChangeNeeded(int orientation) {
        return orientation != 0;
    }

    public static void changeOrientation(Bitmap bitmap, Matrix matrix, int frontBack, int orientation) {
        if (frontBack == Constants.CAMERA_BACK_FRAGMENT) {
            if (bitmap.getWidth() < bitmap.getHeight()) {
                matrix.postRotate(orientation);
            } else {
                matrix.postRotate(-orientation);
            }
        } else if (frontBack == Constants.PHOTO_FRAGMENT) {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                matrix.postRotate(orientation);
            } else {
                matrix.postRotate(-orientation);
            }
        }
    }

    @Nullable public static Bitmap decodeBitmap(File file, BitmapFactory.Options options) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeBitmap(byte[] data, BitmapFactory.Options options) {
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap decodeSampledBitmap(byte[] data, int reqWidth, int reqHeight) {
        long time = System.currentTimeMillis();
        // first decode check the raw image dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeBitmap(data, options);

        // calculate the factor to scale down by depending on the desired height
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inScaled = false;
        // decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // measure the complexity time of decoding bitmap
        time = System.currentTimeMillis() - time;
        if (Constants.IS_DEBUG) Log.d(TAG, "complexity time of decoding bitmap is: " + time);

        return decodeBitmap(data, options);
    }

    @Nullable public static Bitmap decodeSampledBitmap(File file, int reqWidth, int reqHeight) {
        long time = System.currentTimeMillis();
        // first decode check the raw image dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeBitmap(file, options);

        // calculate the factor to scale down by depending on the desired height
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inScaled = false;
        // decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // measure the complexity time of decoding bitmap
        time = System.currentTimeMillis() - time;
        if (Constants.IS_DEBUG) Log.d(TAG, "complexity time of decoding bitmap is: " + time);

        return decodeBitmap(file, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // raw height and width of image
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        // calculate the factor to scale down by depending on the desired height
        int inSampleSize = 1;
        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            int heightRatio, widthRatio;
            if (isRoundUpNeeded(imageHeight, reqHeight))
                heightRatio = (int) Math.ceil((float) imageHeight / reqHeight);
            else
                heightRatio = imageHeight / reqHeight;
            if (isRoundUpNeeded(imageWidth, reqWidth))
                widthRatio = (int) Math.ceil((float) imageWidth / reqWidth);
            else
                widthRatio = imageWidth / reqWidth;
            // choose the smallest factor to scale down by, so the scaled image is always slightly larger than needed
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private static boolean isRoundUpNeeded(int imageSize, int reqSize) {
        int ratio = imageSize / reqSize;
        float fractionalRatio = (float) imageSize / reqSize;
        return fractionalRatio - ratio > 0.5;
    }

    public static DisplayMetrics getDisplaySize(Activity activity) {
        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static void copy(final File src, final File dst) {
        try {
            long time = System.currentTimeMillis();
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            if (Constants.IS_DEBUG)
                Log.d(TAG, "Time to com.siavash.dualcamera.util.copy: " + String.valueOf(System.currentTimeMillis() - time) + " - in the thread: " + Thread.currentThread().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
