package com.siavash.dualcamera.util

import android.content.Context
import android.graphics.Typeface
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.siavash.dualcamera.R
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import java.util.jar.Attributes

/**
 * Created by sia on 11/16/15.
 */
class Toolbar : Toolbar {
    private val title: TextView by bindView(R.id.toolbar_title)
    val right: ImageButton by bindView(R.id.toolbar_right)
    val left: ImageButton by bindView(R.id.toolbar_left)

    constructor(context: Context) : super(context) {
        setUp(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUp(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUp(context)
    }

    private fun setUp(context: Context) {
        context.layoutInflater.inflate(R.layout.layout_toolbar, this, true)
    }

    fun setTitle(title: String) {
        this.title.text = title
    }

    fun setTitleStyle(typeface: Typeface) {
        title.typeface = typeface
    }

    fun defaultTitleStyle() {
        title.typeface = getFont(context, Font.AFSANEH)
    }

    fun setRightItemVisibility(visibility: Int) {
        right.visibility = visibility
    }

    inline fun setRightAction(crossinline function: () -> Unit) {
        if (right.visibility != VISIBLE) setRightItemVisibility(VISIBLE)
        right.onClick {
            function()
        }
    }

    inline fun setLeftAction(crossinline function: () -> Unit) {
        left.onClick {
            function()
        }
    }

    fun defaultHamburgerAction(drawer: DrawerLayout) {
        left.onClick {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.openDrawer(GravityCompat.END)
            }
        }
    }
}