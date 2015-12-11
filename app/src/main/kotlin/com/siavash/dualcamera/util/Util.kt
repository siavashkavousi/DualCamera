package com.siavash.dualcamera.util

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Typeface
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.siavash.dualcamera.ApplicationBase
import com.siavash.dualcamera.R
import jp.wasabeef.glide.transformations.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

const val orientation = 90
const val compressQuality = 90
const val finalImageUrl = ".dualImageUrl"

// thread pool and thread related vars
private val coreCount = Runtime.getRuntime().availableProcessors()
val executor = Executors.newFixedThreadPool(coreCount + 1)
val cameraPhotoDoneSignal = ResettableCountDownLatch(2)

// animation constants
const val shortAnimTime = 200L
const val mediumAnimTime = 400L
const val longAnimTime = 600L

// camera images path
val frontImagePath = getExternalApplicationStorage() + File.separator + CameraId.FRONT.address
val backImagePath = getExternalApplicationStorage() + File.separator + CameraId.BACK.address

fun getExternalApplicationStorage(): String {
    return getExternalStorageDirectoryPath(ApplicationBase.appName)
}

fun getExternalStorageDirectoryPath(appName: String): String {
    val dir = File(Environment.getExternalStorageDirectory(), appName)
    if (!dir.exists() && !dir.mkdirs()) throw NullPointerException("File directory not found")

    return dir.absolutePath
}

fun getOutputMediaFilePath(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
    return getExternalApplicationStorage() + File.separator + "IMG_$timeStamp.jpg"
}

fun View.saveBitmap(file: File): String {
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
fun Bitmap.encodeBitmap(targetFile: File): String {
    this.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(targetFile))
    return targetFile.absolutePath
}

/**
 * Loads sampled bitmap from file
 * @param imagePath File path which contains bitmap
 * @param reqWidth Requested width
 * @param reqHeight Requested height
 * @return decoded bitmap
 */
fun decodeSampledBitmap(imagePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
    // first decode check the raw image dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imagePath, options)

    // calculate the factor to scale down by depending on the desired height
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inScaled = false
    options.inJustDecodeBounds = false

    return BitmapFactory.decodeFile(imagePath, options)
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize > reqHeight) && (halfWidth / inSampleSize > halfWidth)) inSampleSize *= 2
    }
    return inSampleSize
}


/**
 * Copies file from source to destination
 * @param src source file
 * @param dst destination file
 */
fun File.copy(src: File, dst: File) {
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

fun getDisplaySize(activity: Activity): Point {
    val p = Point()
    activity.windowManager.defaultDisplay.getSize(p)
    return p
}

fun FragmentManager.addFragment(container: Int, fragment: Fragment, tag: String? = null, animEnter: Int = 0, animExit: Int = 0, animPopEnter: Int = 0, animPopExit: Int = 0) {
    this.beginTransaction()
            .setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit)
            .add(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
}

fun FragmentManager.replaceFragment(container: Int, fragment: Fragment, tag: String? = null, animEnter: Int = 0, animExit: Int = 0, animPopEnter: Int = 0, animPopExit: Int = 0) {
    this.beginTransaction()
            .setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit)
            .replace(container, fragment, tag)
            .commit()
}

fun getAppMainFont(context: Context): Typeface {
    return getFont(context, Font.AFSANEH)
}

fun getDefaultPoemFont(context: Context): Typeface {
    return getFont(context, Font.MASHIN_TAHRIR)
}

fun getFont(context: Context, fontName: Font): Typeface {
    var resource: String = "fonts/" + fontName.resourceId
    return Typeface.createFromAsset(context.assets, resource)
}

/**
 * Returns the index of the smallest element or `null` if there are no elements.
 */
fun FloatArray.minElementIndex(): Int? {
    if (isEmpty()) return null
    var minIndex = 0
    var min = this[0]
    for (i in 1..lastIndex) {
        val e = this[i]
        if (min > e) {
            minIndex = i
            min = e
        }
    }
    return minIndex
}

inline fun doAsyncAndWait(ctx: Context, message: String, crossinline function: () -> Unit) {
    val progressDialog = ProgressDialog(ctx)
    progressDialog.setMessage("در حال پردازش داب شما!")
    progressDialog.setCancelable(false)
    progressDialog.show()
    executor.execute { function() }
    progressDialog.dismiss()
}

inline fun doAsyncAndWaitThenShowResult(ctx: Context, message: String, crossinline function: () -> Unit, result: () -> Unit) {
    val progressDialog = ProgressDialog(ctx)
    progressDialog.setMessage(message)
    progressDialog.setCancelable(false)
    progressDialog.show()
    executor.execute { function() }
    progressDialog.dismiss()
    result()
}

fun dip2px(ctx: Context, dp: Float): Int {
    val scale = ctx.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun ImageView.setImageWithTransformation(path: String, transformationType: TransformationType) {
    when (transformationType) {
        TransformationType.Mask ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(CenterCrop(context), MaskTransformation(context, R.drawable.mask_starfish))
                    .into(this)

        TransformationType.NinePatchMask ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(CenterCrop(context), MaskTransformation(context, R.drawable.mask_chat_right))
                    .into(this)

        TransformationType.CropTop ->
            Glide.with(context).load(path)
                    .bitmapTransform(CropTransformation(context, 30, 30, CropTransformation.CropType.TOP))
                    .into(this)

        TransformationType.CropCenter ->
            Glide.with(context).load(path)
                    .bitmapTransform(CropTransformation(context, 30, 30))
                    .into(this)

        TransformationType.CropBottom ->
            Glide.with(context).load(path)
                    .bitmapTransform(CropTransformation(context, 30, 30, CropTransformation.CropType.BOTTOM))
                    .into(this)

        TransformationType.CropSquare ->
            Glide.with(context).load(path)
                    .bitmapTransform(CropSquareTransformation(context))
                    .into(this)

        TransformationType.CropCircle ->
            Glide.with(context).load(path)
                    .bitmapTransform(CropCircleTransformation(context))
                    .into(this)

        TransformationType.GrayScale ->
            Glide.with(context).load(path)
                    .bitmapTransform(GrayscaleTransformation(context))
                    .into(this)

        TransformationType.RoundedCorners ->
            Glide.with(context).load(path)
                    .bitmapTransform(RoundedCornersTransformation(context, 3, 0, RoundedCornersTransformation.CornerType.BOTTOM))
                    .into(this)
    }
}