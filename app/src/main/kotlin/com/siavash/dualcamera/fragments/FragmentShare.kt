package com.siavash.dualcamera.fragments

import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.siavash.dualcamera.R
import com.siavash.dualcamera.activities.ActivityPhoto
import com.siavash.dualcamera.utils.*
import org.jetbrains.anko.act
import org.jetbrains.anko.ctx
import java.io.File

/**
 * Created by sia on 11/2/15.
 */
class FragmentShare : Fragment() {
    private val socialNetworks: List<Button> by bindViews(R.id.facebook, R.id.whatsapp, R.id.telegram, R.id.instagram, R.id.line, R.id.more)
    private val shareText: TextView by bindView(R.id.share_to)
    private val image: ImageView by bindView(R.id.photo_container)

    private lateinit var displaySize: Point

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fun setUpToolbar() {
            val toolbar = (act as ActivityPhoto).toolbar
            toolbar.setTitle("اشتراک گذاری")
            toolbar.setLeftItemVisibility(View.GONE)
        }

        val view = inflater.inflate(R.layout.fragment_share, container, false)
        if (act is ActivityPhoto) setUpToolbar()
        displaySize = getDisplaySize(act)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        fun loadBitmap() {
            Glide.with(ctx).load(finalImagePath)
                    .fitCenter()
                    .crossFade()
                    .into(image)
        }

        fun setTypefaces() {
            shareText.typeface = getFont(activity, Font.AFSANEH)
            for (button in socialNetworks) {
                button.typeface = getFont(activity, Font.NAZANIN_BOLD)
            }
        }

        fun setListeners() {
            val onClickListener = OnClickListener()
            for (item in socialNetworks) {
                item.setOnClickListener(onClickListener)
            }
        }

        super.onActivityCreated(savedInstanceState)

        loadBitmap()
        setTypefaces()
        setListeners()
    }


    private inner class OnClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val id = v.id
            if (id == socialNetworks[0].id) {
                shareIntent("facebook", "فیسبوک")
            } else if (id == socialNetworks[1].id) {
                shareIntent("whatsapp", "واتس اپ")
            } else if (id == socialNetworks[2].id) {
                shareIntent("telegram", "تلگرام")
            } else if (id == socialNetworks[3].id) {
                shareIntent("instagram", "اینستاگرام")
            } else if (id == socialNetworks[4].id) {
                shareIntent("line", "لاین")
            } else if (id == socialNetworks[5].id) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("image/jpg")
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(finalImagePath)))
                startActivity(intent)
            }
        }

        private fun shareIntent(intentName: String, socialName: String) {
            fun generateCustomIntent(prototype: Intent, appNameToShareWith: String, appNameInPersian: String) {
                val resInfo = activity.packageManager.queryIntentActivities(prototype, PackageManager.MATCH_DEFAULT_ONLY)
                var resolved = false
                if (!resInfo.isEmpty()) {
                    for (resolveInfo in resInfo) {
                        if (resolveInfo.activityInfo.name.contains(appNameToShareWith)) {
                            prototype.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
                            resolved = true
                            break
                        }
                    }
                }

                if (resolved)
                    startActivity(prototype)
                else {
                    Toast.makeText(activity, appNameInPersian + " نصب نیست!", Toast.LENGTH_LONG).show()
                }
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("image/jpg")
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(finalImagePath)))
            generateCustomIntent(intent, intentName, socialName)
        }
    }
}