package com.siavash.dualcamera.fragments

import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.siavash.dualcamera.R
import com.siavash.dualcamera.activities.PhotoActivity
import com.siavash.dualcamera.util.*
import org.jetbrains.anko.act
import java.io.File

/**
 * Created by sia on 11/2/15.
 */
class ShareFragment : Fragment() {
    val socialNetworks: List<Button> by bindViews(R.id.facebook, R.id.whatsapp, R.id.telegram, R.id.instagram, R.id.line, R.id.more)
    val shareText: TextView by bindView(R.id.share_to)
    val image: ImageView by bindView(R.id.photo_container)
    var imageUrl: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)

        imageUrl = arguments.getString(finalImageUrl)
        val bitmap = decodeSampledBitmap(File(imageUrl), metrics.widthPixels, metrics.heightPixels)
        image.setImageBitmap(bitmap)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share, container, false)
        (act as PhotoActivity).toolbarTitle.text = "اشتراک گذاری"

        setTypefaces()
        setListeners()
        return view
    }

    private fun setTypefaces() {
        //fixme string utils should be modified to kotlin equivalent
        shareText.typeface = StringUtil.getFont(activity, StringUtil.FONT_AFSANEH)
        for (button in socialNetworks) {
            button.typeface = StringUtil.getFont(activity, StringUtil.FONT_NAZANIN_BOLD)
        }
    }

    private fun setListeners() {
        val onClickListener = OnClickListener()
        socialNetworks[0].setOnClickListener(onClickListener)
        socialNetworks[1].setOnClickListener(onClickListener)
        socialNetworks[2].setOnClickListener(onClickListener)
        socialNetworks[3].setOnClickListener(onClickListener)
        socialNetworks[4].setOnClickListener(onClickListener)
        socialNetworks[5].setOnClickListener(onClickListener)
    }

    private fun shareIntent(intentName: String, socialName: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/jpg")
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(imageUrl)))
        generateCustomIntent(intent, intentName, socialName)
    }


    private fun generateCustomIntent(prototype: Intent, appNameToShareWith: String, appNameInPersian: String) {
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
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(imageUrl)))
                startActivity(intent)
            }
        }
    }
}