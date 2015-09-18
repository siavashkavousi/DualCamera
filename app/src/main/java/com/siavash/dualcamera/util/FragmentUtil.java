package com.siavash.dualcamera.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Created by sia on 9/18/15.
 */
public class FragmentUtil {
    public static void switchFragment(FragmentManager fragmentManager, int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
    }
}
