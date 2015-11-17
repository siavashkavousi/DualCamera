package com.siavash.dualcamera.util

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Typeface
import android.os.Environment
import android.view.View
import com.siavash.dualcamera.ApplicationBase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

internal val orientation = 90
internal val compressQuality = 90
internal val finalImageUrl = ".dualImageUrl"
internal val countDownLatch = ResettableCountDownLatch(2)
internal val executor = Executors.newFixedThreadPool(3)

internal fun getExternalApplicationStorage(): String {
    return getExternalStorageDirectoryPath(ApplicationBase.appName)
}

internal fun getExternalStorageDirectoryPath(appName: String): String {
    val dir = File(Environment.getExternalStorageDirectory(), appName)
    if (!dir.exists() && !dir.mkdirs()) throw NullPointerException("File directory not found")

    return dir.absolutePath
}

internal fun getOutputMediaFilePath(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
    return getExternalApplicationStorage() + File.separator + "IMG_$timeStamp.jpg"
}

internal fun View.saveBitmap(file: File): String {
    this.isDrawingCacheEnabled = true
    val bitmap = this.drawingCache
    val imageUrl = bitmap.encodeBitmap(file)
    this.isDrawingCacheEnabled = false
    return imageUrl
}

/**
 * Saves bitmap into file
 * @param targetFile Target file in order to save bitmap into it
 * @return Absolute path to the saved bitmap file
 */
internal fun Bitmap.encodeBitmap(targetFile: File): String {
    this.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(targetFile))
    return targetFile.absolutePath
}

/**
 * Loads sampled bitmap from file
 * @param file File which contains bitmap
 * @param reqWidth Requested width
 * @param reqHeight Requested height
 * @return decoded bitmap
 */
internal fun decodeSampledBitmap(file: File, reqWidth: Int, reqHeight: Int): Bitmap {
    // first decode check the raw image dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    Util.decodeBitmap(file, options)

    // calculate the factor to scale down by depending on the desired height
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inScaled = false
    options.inJustDecodeBounds = false

    return Util.decodeBitmap(file, options)
}

/**
 * Loads sampled bitmap from data array
 * @param data data array
 * @param reqWidth Requested width
 * @param reqHeight Requested height
 * @return decoded bitmap
 */
internal fun decodeSampledBitmap(data: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {
    // first decode check the raw image dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    Util.decodeBitmap(data, options)

    // calculate the factor to scale down by depending on the desired height
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inScaled = false
    options.inJustDecodeBounds = false

    return Util.decodeBitmap(data, options)
}

/**
 * Loads bitmap from file
 */
internal fun decodeBitmap(file: File, options: BitmapFactory.Options): Bitmap {
    return BitmapFactory.decodeStream(FileInputStream(file), null, options)
}

/**
 * Loads bitmap from data array
 */
internal fun decodeBitmap(data: ByteArray, options: BitmapFactory.Options): Bitmap {
    return BitmapFactory.decodeByteArray(data, 0, data.size, options)
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // raw height and width of image
    val imageHeight = options.outHeight
    val imageWidth = options.outWidth
    // calculate the factor to scale down by depending on the desired height
    var inSampleSize = 1
    if (imageHeight > reqHeight || imageWidth > reqWidth) {
        val heightRatio: Int
        val widthRatio: Int
        if (isRoundUpNeeded(imageHeight, reqHeight))
            heightRatio = Math.ceil((imageHeight.toFloat() / reqHeight).toDouble()).toInt()
        else
            heightRatio = imageHeight / reqHeight
        if (isRoundUpNeeded(imageWidth, reqWidth))
            widthRatio = Math.ceil((imageWidth.toFloat() / reqWidth).toDouble()).toInt()
        else
            widthRatio = imageWidth / reqWidth
        // choose the smallest factor to scale down by, so the scaled image is always slightly larger than needed
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }

    return inSampleSize
}

private fun isRoundUpNeeded(imageSize: Int, reqSize: Int): Boolean {
    val ratio = imageSize / reqSize
    val fractionalRatio = imageSize.toFloat() / reqSize
    return fractionalRatio - ratio > 0.5
}

/**
 * Copies file from source to destination
 * @param src source file
 * @param dst destination file
 */
internal fun File.copy(src: File, dst: File) {
    try {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        val inChannel = inStream.channel
        val outChannel = outStream.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

internal fun getDisplaySize(activity: Activity): Point {
    val p = Point()
    activity.windowManager.defaultDisplay.getSize(p)
    return p
}

internal fun FragmentManager.addFragment(container: Int, fragment: Fragment, tag: String? = null, animEnter: Int = 0, animExit: Int = 0, animPopEnter: Int = 0, animPopExit: Int = 0) {
    this.beginTransaction()
            .setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit)
            .add(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
}

internal fun FragmentManager.replaceFragment(container: Int, fragment: Fragment, tag: String? = null, animEnter: Int = 0, animExit: Int = 0, animPopEnter: Int = 0, animPopExit: Int = 0) {
    this.beginTransaction()
            .setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit)
            .replace(container, fragment, tag)
            .commit()
}

internal fun getAppMainFont(context: Context): Typeface {
    return getFont(context, Font.AFSANEH)
}

internal fun getDefaultPoemFont(context: Context): Typeface {
    return getFont(context, Font.MASHIN_TAHRIR)
}

internal fun getFont(context: Context, fontName: Font): Typeface {
    var resource: String = "fonts/" + fontName.resourceId
    return Typeface.createFromAsset(context.assets, resource)
}