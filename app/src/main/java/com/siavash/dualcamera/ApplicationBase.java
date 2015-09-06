package com.siavash.dualcamera;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by sia on 9/3/15.
 */
public class ApplicationBase extends Application {
    private RefWatcher refWatcher;

    @Override public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context){
        ApplicationBase appBase = (ApplicationBase) context.getApplicationContext();
        return appBase.refWatcher;
    }
}
