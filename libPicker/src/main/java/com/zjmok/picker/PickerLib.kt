package com.zjmok.picker

import android.content.Context

object PickerLib {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun isInit(): Boolean {
        return appContext != null
    }

    internal fun checkInit() {
        if (appContext == null) {
            throw IllegalStateException("PickerLib not initialized!")
        }
    }

    // 提供获取 Context 的方法，确保非空
    internal fun getAppContext(): Context {
        checkInit()
        return appContext!!
    }

}