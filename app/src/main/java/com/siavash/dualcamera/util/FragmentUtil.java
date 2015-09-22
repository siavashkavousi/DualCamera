package com.siavash.dualcamera.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Created by sia on 9/18/15.
 */
public class FragmentUtil {
    public static void addFragment(FragmentManager fragmentManager, int container, Fragment fragment) {
        addFragment(fragmentManager, container, fragment, 0, 0, 0, 0);
    }

    public static void addFragment(FragmentManager fragmentManager, int container, Fragment fragment, int animEnter, int animExit, int animPopEnter, int animPopExit) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
        fragmentTransaction.add(container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, int container, Fragment fragment) {
        replaceFragment(fragmentManager, container, fragment, 0, 0, 0, 0);
    }

    public static void replaceFragment(FragmentManager fragmentManager, int container, Fragment fragment, int animEnter, int animExit, int animPopEnter, int animPopExit) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
    }
}
