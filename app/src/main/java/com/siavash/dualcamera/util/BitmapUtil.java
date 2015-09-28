package com.siavash.dualcamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Utility for saving, loading, rotating, resizing and ... of bitmaps
 * Created by sia on 8/18/15.
 */
public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    @NonNull public static File getFile(String url) {
        File file = new File(url);
        if (!file.exists())
            throw new NullPointerException("requested file does not exist in the underlying system");
        return file;
    }

    @NonNull public static File getCacheFile(Context context, String url) {
        File file = new File(context.getCacheDir(), url);
        if (!file.exists())
            throw new NullPointerException("requested file does not exist in the underlying system");
        return file;
    }

    @Nullable public static File setImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = "IMG_" + timeStamp + ".jpg";
        return setFile(name);
    }

    @Nullable public static File setFile(String name) {
        File imageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DualCamera");

        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                if (Constants.IS_DEBUG) Log.d(TAG, "Required media storage does not exist");
                return null;
            }
        }

        return new File(imageDir.getPath() + File.separator + name + ".jpg");
    }

    public static String save(Context context, View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        String imageUrl = BitmapUtil.save(context, bitmap, BitmapUtil.setFile(Constants.IMAGE_URL));
        view.setDrawingCacheEnabled(false);
        return imageUrl;
    }

    @Nullable public static String save(Context context, final Bitmap bitmap, final File targetFile) {
        return save(context, bitmap, targetFile, null);
    }

    /**
     * Saves bitmap into storage
     *
     * @param context    context of the related activity
     * @param bitmap     target bitmap
     * @param targetFile target file in order to save bitmap into it
     * @return absolute path to the saved bitmap
     */
    @Nullable public static <T extends Observer> String save(Context context, final Bitmap bitmap, final File targetFile, T observer) {
        if (targetFile == null) {
            Toast.makeText(context, "Image retrieval failed.", Toast.LENGTH_SHORT).show();
            return null;
        }
        Observable observable = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos);
            }
        });

        Subscription subscription;
        if (observer != null) {
            subscription = observable.subscribe(observer);
        } else {
            subscription = observable.subscribe();
        }
        ApplicationBase.getRefWatcher(context).watch(subscription);

        return targetFile.getAbsolutePath();
    }

    public static void save(Context context, byte[] data, int frontBack, String url, int orientation) {
        save(context, data, frontBack, url, orientation, null);
    }

    /**
     * save photo in cache folder of app asynchronously
     *
     * @param context     context need to access cache folder
     * @param data        to be saved
     * @param url         place to save in cache folder
     * @param orientation orientation of the taken photo
     */
    public static <T extends Observer> void save(Context context, final byte[] data, final int frontBack, String url, final int orientation, T observer) {
        final long time = System.currentTimeMillis();

        final File file = new File(context.getCacheDir(), url);
        Observable observable = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (orientation != 0) {
                    Matrix matrix = new Matrix();
                    if (frontBack == Constants.CAMERA_BACK_FRAGMENT) {
                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            matrix.postRotate(-orientation);
                        } else {
                            matrix.postRotate(orientation);
                        }
                    } else if (frontBack == Constants.PHOTO_FRAGMENT) {
                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            matrix.postRotate(orientation);
                        } else {
                            matrix.postRotate(-orientation);
                        }
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, fos);
                bitmap.recycle();
                if (Constants.IS_DEBUG)
                    Log.d(TAG, "save bitmap with orientation: " + String.valueOf(System.currentTimeMillis() - time) + " - in the thread: " + Thread.currentThread().toString());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation());

        Subscription subscription;
        if (observer != null) {
            subscription = observable.subscribe(observer);
        } else {
            subscription = observable.subscribe();
        }

        ApplicationBase.getRefWatcher(context).watch(subscription);
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

    @Nullable public static Bitmap decodeSampledBitmap(String url, int reqWidth, int reqHeight) {
        return decodeSampledBitmap(null, url, reqWidth, reqHeight);
    }

    @Nullable public static Bitmap decodeSampledBitmap(Context context, String url, int reqWidth, int reqHeight) {
        long time = System.currentTimeMillis();
        // first decode check the raw image dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        File file;
        if (context == null) {
            file = getFile(url);
        } else {
            file = getCacheFile(context, url);
        }
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

        return inSampleSize;
    }

    public static void copy(final File src, final File dst) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
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
                        Log.d(TAG, "Time to copy: " + String.valueOf(System.currentTimeMillis() - time) + " - in the thread: " + Thread.currentThread().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.computation()).subscribe();
    }
}
