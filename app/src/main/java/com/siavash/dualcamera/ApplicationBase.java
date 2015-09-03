package com.siavash.dualcamera;

import android.app.Application;
import android.content.Context;

/**
 * Created by sia on 9/3/15.
 */
public class ApplicationBase extends Application {
    private static Context sContext;

    @Override public void onCreate() {
        super.onCreate();
        ApplicationBase.sContext = getApplicationContext();
    }

    public static Context getAppContext(){
        return ApplicationBase.sContext;
    }
}
