package com.siavash.dualcamera;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by sia on 9/3/15.
 */
public class ApplicationBase extends Application {
    private static String sAppName;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        ApplicationBase appBase = (ApplicationBase) context.getApplicationContext();
        return appBase.refWatcher;
    }

    public static String getAppName() {
        return ApplicationBase.sAppName;
    }

    @Override public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.SIGNATURE_MATCH);
            sAppName = appInfo != null ? (String) getPackageManager().getApplicationLabel(appInfo) : "unknown";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
