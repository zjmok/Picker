package com.zjmok.picker.demo

import android.app.Application

class App : Application() {

    companion object {
        lateinit var INSTANCE: Application
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

    }

}
