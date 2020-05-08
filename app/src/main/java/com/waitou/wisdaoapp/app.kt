package com.waitou.wisdaoapp

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.zxy.tiny.Tiny

/**
 * auth aboom
 * date 2019-06-04
 */
class app : Application() {

    override fun onCreate() {
        super.onCreate()
        Tiny.getInstance().init(this);

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
