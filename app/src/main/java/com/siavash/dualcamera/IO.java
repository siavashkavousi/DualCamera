package com.siavash.dualcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sia on 8/18/15.
 */
public class IO {
    private static final String TAG = IO.class.getSimpleName();

    public static void save(Context context, byte[] data, String url){
        File file = new File(context.getCacheDir(), url);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap bitmap =BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.d(TAG, "bitmap file saved successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    @Nullable public static Bitmap loadBitmap(Context context, String url){
        File file = new File(context.getCacheDir(), url);
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
