package com.siavash.dualcamera

import android.app.Application
import android.content.pm.PackageManager
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

/**
 * Created by sia on 11/11/15.
 */
class ApplicationBase : Application() {
    companion object ApplicationName {
        lateinit var appName: String
    }

    override fun onCreate() {
        super.onCreate()

        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.SIGNATURE_MATCH)
        appName = packageManager.getApplicationLabel(appInfo) as String

        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit);
        Fabric.with(this, Answers());
    }
}