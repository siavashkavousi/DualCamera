package com.siavash.dualcamera.utils

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.siavash.dualcamera.ApplicationBase
import com.siavash.dualcamera.R
import com.siavash.dualcamera.utils.ResettableCountDownLatch
import jp.wasabeef.glide.transformations.CropCircleTransformation
import jp.wasabeef.glide.transformations.GrayscaleTransformation
import jp.wasabeef.glide.transformations.MaskTransformation
import jp.wasabeef.glide.transformations.gpu.*
import org.jetbrains.anko.defaultSharedPreferences
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

const val compressQuality = 90

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
val finalImagePath = getExternalApplicationStorage() + File.separator + ".dualImageUrl"

const val frontImageOrientation = -90
const val backImageOrientation = 90

// shared preferences
const val commentKey = "commentKey"
const val commentCounter = 5

fun setCommentCounter(context: Context, value: Int = commentCounter) {
    context.defaultSharedPreferences.edit().putInt(commentKey, value).apply()
}

fun getCommentCounter(context: Context): Int {
    return context.defaultSharedPreferences.getInt(commentKey, 0)
}

fun isCommentAllowed(context: Context): Boolean {
    val value = getCommentCounter(context)
    if (value > 0) {
        setCommentCounter(context, value - 1)
        return false
    } else {
        setCommentCounter(context)
        return true
    }
}

fun sendIntentForCommentInCafeBazaar(context: Context) {
    if (isCommentAllowed(context)) {
        try {
            val intent = Intent(Intent.ACTION_EDIT);
            intent.setData(Uri.parse("bazaar://details?id=" + context.packageName));
            intent.setPackage("com.farsitel.bazaar");
            context.startActivity(intent);
        } catch(e: ActivityNotFoundException) {
            Toast.makeText(context, "بازار نصب نیست!", Toast.LENGTH_LONG).show()
        }
    }
}

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
    progressDialog.setMessage(message)
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
        TransformationType.Star ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , MaskTransformation(context, R.drawable.mask_starfish))
                    .into(this)

        TransformationType.Messenger ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , MaskTransformation(context, R.drawable.mask_chat_right))
                    .into(this)

        TransformationType.Flower ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , MaskTransformation(context, R.drawable.flower))
                    .into(this)

        TransformationType.CropCircle ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , CropCircleTransformation(context))
                    .into(this)

        TransformationType.GrayScale ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , GrayscaleTransformation(context))
                    .into(this)

        TransformationType.Vignette ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , VignetteFilterTransformation(context))
                    .into(this)

        TransformationType.Brightness ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , BrightnessFilterTransformation(context))
                    .into(this)

        TransformationType.Swirl ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , SwirlFilterTransformation(context))
                    .into(this)

        TransformationType.Sketch ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , SketchFilterTransformation(context))
                    .into(this)

        TransformationType.Pixelation ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , PixelationFilterTransformation(context))
                    .into(this)

        TransformationType.Invert ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , InvertFilterTransformation(context))
                    .into(this)

        TransformationType.Contrast ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , ContrastFilterTransformation(context))
                    .into(this)

        TransformationType.Sepia ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , SepiaFilterTransformation(context))
                    .into(this)

        TransformationType.Toon ->
            Glide.with(context).load(path)
                    .fitCenter()
                    .bitmapTransform(RotateTransformation(context, frontImageOrientation)
                            , ToonFilterTransformation(context))
                    .into(this)
    }
}