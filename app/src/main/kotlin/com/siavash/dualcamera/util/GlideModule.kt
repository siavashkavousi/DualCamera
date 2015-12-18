package com.siavash.dualcamera.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter
import com.bumptech.glide.load.engine.cache.MemoryCacheAdapter
import com.bumptech.glide.module.GlideModule

/**
 * Created by sia on 12/18/15.
 */
class GlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(DiskCacheAdapter())
        builder.setBitmapPool(BitmapPoolAdapter())
        builder.setMemoryCache(MemoryCacheAdapter())
    }

    override fun registerComponents(context: Context?, glide: Glide?) {
    }
}