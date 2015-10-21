package com.siavash.dualcamera.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.R;
import com.siavash.dualcamera.fragments.OnFragmentInteractionListener;
import com.siavash.dualcamera.fragments.PhotoFragment;
import com.siavash.dualcamera.fragments.ShareFragment;
import com.siavash.dualcamera.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sia on 10/21/15.
 */
public class PhotoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;
    @Bind(R.id.nav_view) NavigationView navigationView;

    private PhotoFragment photoFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        photoFragment = PhotoFragment.getInstance();
        switchFragmentTo(Constants.PHOTO_FRAGMENT);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @OnClick(R.id.toolbar_hamburger) void onToolbarToggleClick() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    @Override public void switchFragmentTo(int fragmentName, String... optionalValues) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentName == Constants.PHOTO_FRAGMENT) {
            FragmentUtil.replaceFragment(fragmentManager, R.id.container, photoFragment);
        } else if (fragmentName == Constants.SHARE_FRAGMENT) {
            FragmentUtil.replaceFragment(fragmentManager, R.id.container, ShareFragment.newInstance(optionalValues[0]));
        }
    }

    @Override public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_photo_fragment) {
            switchFragmentTo(Constants.PHOTO_FRAGMENT);
        } else if (id == R.id.nav_share_fragment) {
            switchFragmentTo(Constants.SHARE_FRAGMENT);
        }

        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }
}
