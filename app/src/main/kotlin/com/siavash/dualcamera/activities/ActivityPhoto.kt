package com.siavash.dualcamera.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.siavash.dualcamera.R
import com.siavash.dualcamera.fragments.FragmentPhoto
import com.siavash.dualcamera.fragments.FragmentShare
import com.siavash.dualcamera.fragments.OnFragmentInteractionListener
import com.siavash.dualcamera.utils.*
import org.jetbrains.anko.intentFor

/**
 * Created by sia on 10/31/15.
 */
class ActivityPhoto : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    private val drawer: DrawerLayout by bindView(R.id.drawer_layout)
    val toolbar: Toolbar by bindView(R.id.toolbar)

    private val navigationView: NavigationView by bindView(R.id.nav_view)

    private var fragmentPhoto: FragmentPhoto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(toolbar)

        switchFragmentTo(FragmentId.PHOTO)
        navigationView.setNavigationItemSelectedListener(this)
        toolbar.defaultHamburgerAction(drawer)
        toolbar.defaultTitleStyle()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_camera_fragment) {
            startActivity(intentFor<ActivityCamera>().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
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
        if (fragmentId.name == getCurrentFragmentTag()) return

        val fragment = fragmentManager.findFragmentByTag(fragmentId.name)
        if (fragmentId == FragmentId.PHOTO) {
            if (fragment is FragmentPhoto)
                fragmentManager.popBackStack()
            else {
                fragmentPhoto = FragmentPhoto()
                fragmentManager.addFragment(R.id.container, fragmentPhoto as FragmentPhoto, fragmentId.name)
            }
        } else if (fragmentId == FragmentId.SHARE) {
            fragmentPhoto?.saveBitmapHidden(finalImagePath)
            fragmentManager.addFragment(R.id.container, FragmentShare(), FragmentId.SHARE.name)
        }
    }

    private fun getCurrentFragmentTag(): String? {
        if (fragmentManager.backStackEntryCount > 0) {
            return fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
        } else
            return ""
    }
}