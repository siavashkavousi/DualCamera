package com.siavash.dualcamera.util

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.bitmap.TransformationUtils

/**
 * Created by sia on 12/13/15.
 */
class RotateTransformation : Transformation<Bitmap> {
    private lateinit var bitmapPool: BitmapPool
    private var orientation: Int = 0

    constructor(context: Context, orientation: Int) {
        bitmapPool = Glide.get(context).bitmapPool
        this.orientation = orientation
    }

    constructor(bitmapPool: BitmapPool, orientation: Int) {
        this.bitmapPool = bitmapPool
        this.orientation = orientation
    }

    override fun transform(resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap>? {
        var source = resource.get()

        source = TransformationUtils.fitCenter(source, bitmapPool, outHeight, outWidth)
        source = TransformationUtils.rotateImage(source, orientation)

        return BitmapResource.obtain(source, bitmapPool)
    }

    override fun getId(): String? {
        return "RotateTransformation()"
    }
}