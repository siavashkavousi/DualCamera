package com.siavash.dualcamera.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import com.siavash.dualcamera.R
import com.siavash.dualcamera.fragments.OnFragmentInteractionListener
import com.siavash.dualcamera.fragments.PhotoFragment
import com.siavash.dualcamera.fragments.ShareFragment
import com.siavash.dualcamera.util.FragmentId
import com.siavash.dualcamera.util.addFragment
import com.siavash.dualcamera.util.bindView
import com.siavash.dualcamera.util.finalImageUrl
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.onClick
import org.jetbrains.anko.withArguments

/**
 * Created by sia on 10/31/15.
 */
class PhotoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    val drawer: DrawerLayout by bindView(R.id.drawer_layout)
    val toolbar: Toolbar by bindView(R.id.toolbar)
    val toolbarTitle: TextView by bindView(R.id.toolbar_title)
    val toolbarHamburger: ImageButton by bindView(R.id.toolbar_hamburger)
    val toolbarAction: ImageButton by bindView(R.id.toolbar_action)
    val navigationView: NavigationView by bindView(R.id.nav_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(toolbar)

        switchFragmentTo(FragmentId.PHOTO)
        navigationView.setNavigationItemSelectedListener(this)
        toolbarHamburgerOnClick()
    }

    private fun toolbarHamburgerOnClick() {
        toolbarHamburger.onClick {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.openDrawer(GravityCompat.END)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_camera_fragment) {
            startActivity(intentFor<MainActivity>().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
            finish()
        } else if (id == R.id.nav_photo_fragment) {
            switchFragmentTo(FragmentId.PHOTO)
        } else if (id == R.id.nav_share_fragment) {
            switchFragmentTo(FragmentId.SHARE)
        }
        drawer.closeDrawer(GravityCompat.END)
        return true
    }

    override fun switchFragmentTo(fragmentId: FragmentId, vararg optionalValues: String) {
        val fragment = fragmentManager.findFragmentByTag(fragmentId.name)
        if (fragmentId == FragmentId.PHOTO) {
            if (fragment !is PhotoFragment)
                fragmentManager.addFragment(R.id.container, PhotoFragment(), FragmentId.PHOTO.name)
        } else if (fragmentId == FragmentId.SHARE) {
            if (fragment !is ShareFragment)
                fragmentManager.addFragment(R.id.container, ShareFragment().withArguments(Pair(finalImageUrl, optionalValues[0])), FragmentId.SHARE.name)
        }
    }
}