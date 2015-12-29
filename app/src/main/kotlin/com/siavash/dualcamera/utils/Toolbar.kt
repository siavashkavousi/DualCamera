package com.siavash.dualcamera.utils

import android.content.Context
import android.graphics.Typeface
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import com.siavash.dualcamera.R
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import org.jetbrains.anko.wrapContent

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
        setContentInsetsAbsolute(0, 0)
        minimumWidth = wrapContent
        minimumHeight = 56
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

    fun setLeftItemVisibility(visibility: Int) {
        left.visibility = visibility
    }

    inline fun setLeftAction(crossinline function: () -> Unit) {
        if (left.visibility != VISIBLE) setLeftItemVisibility(VISIBLE)
        left.onClick {
            function()
        }
    }

    inline fun setRightAction(crossinline function: () -> Unit) {
        right.onClick {
            function()
        }
    }

    fun defaultHamburgerAction(drawer: DrawerLayout) {
        right.onClick {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.openDrawer(GravityCompat.END)
            }
        }
    }
}