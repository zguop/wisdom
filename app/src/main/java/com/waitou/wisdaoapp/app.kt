package com.waitou.wisdaoapp

import android.app.Application
import com.zxy.tiny.Tiny

/**
 * auth aboom
 * date 2019-06-04
 */
class app : Application() {

    override fun onCreate() {
        super.onCreate()
        Tiny.getInstance().init(this);

    }
}
