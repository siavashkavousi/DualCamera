package com.siavash.dualcamera.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.siavash.dualcamera.R
import com.siavash.dualcamera.fragments.FragmentPhoto
import com.siavash.dualcamera.fragments.OnTransformationTypeListener
import com.siavash.dualcamera.utils.TransformationType
import com.siavash.dualcamera.utils.bindView
import com.siavash.dualcamera.utils.frontImagePath
import com.siavash.dualcamera.utils.setImageWithTransformation
import org.jetbrains.anko.onClick

/**
 * Created by sia on 12/6/15.
 */
class AdapterTransformation(val parent: FragmentPhoto, val dataSet: List<TransformationType>) : RecyclerView.Adapter<AdapterTransformation.ItemViewHolder>() {
    private val callback: OnTransformationTypeListener by lazy { parent }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transformation, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bindView(dataSet[position])

    override fun getItemCount(): Int = dataSet.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView by bindView(R.id.image)
        private val title: TextView by bindView(R.id.title)

        fun bindView(transformationType: TransformationType) {
            image.setImageWithTransformation(frontImagePath, transformationType)
            title.text = transformationType.name

            image.onClick {
                callback.onTypeSelected(transformationType)
            }
        }
    }
}