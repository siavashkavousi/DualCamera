package com.siavash.dualcamera.util

import android.content.Context
import android.graphics.Typeface
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.support.v7.widget.Toolbar.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import com.siavash.dualcamera.R
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import java.util.jar.Attributes

/**
 * Created by sia on 11/16/15.
 */
class Toolbar(context: Context) : Toolbar(context) {
    val toolbarTitle: TextView by bindView(R.id.toolbar_title)
    val toolbarAction: ImageButton by bindView(R.id.toolbar_action)
    val toolbarHamburger: ImageButton by bindView(R.id.toolbar_hamburger)

    constructor(context: Context, attributes: Attributes?) : this(context) {
        context.layoutInflater.inflate(R.layout.layout_toolbar, this, true)
    }

    constructor(context: Context, attributes: Attributes?, defStyleAttr: Int) : this(context) {
    }

    fun setTitle(title: String) {
        toolbarTitle.text = title
    }

    fun setTitleStyle(typeface: Typeface) {
        toolbarTitle.typeface = typeface
    }

    fun defaultTitleStyle() {
        toolbarTitle.typeface = StringUtil.getFont(context, StringUtil.FONT_AFSANEH)
    }

    fun setActionItemVisibility(visibility: Int) {
        toolbarAction.visibility = visibility
    }

    fun setAction(function: () -> Unit) {
        if (toolbarAction.visibility != VISIBLE) setActionItemVisibility(VISIBLE)
        toolbarAction.onClick {
            function()
        }
    }

    fun setHamburgerAction(function: () -> Unit) {
        toolbarHamburger.onClick {
            function()
        }
    }

    fun defaultHamburgerAction(drawer: DrawerLayout) {
        toolbarHamburger.onClick {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.openDrawer(GravityCompat.END)
            }
        }
    }
}