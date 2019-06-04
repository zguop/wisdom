package com.waitou.wisdaoapp

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * auth aboom
 * date 2019-06-04
 */
class app : Application() {

    override fun onCreate() {
        super.onCreate()
        initLeakCanary(this)
    }

    private fun initLeakCanary(application: Application) {
        /*---------------  内存泄漏的检测 ---------------*/
        if (!BuildConfig.DEBUG || LeakCanary.isInAnalyzerProcess(application)) {
            return
        }
        LeakCanary.install(application)
    }
}
