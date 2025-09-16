package com.hyunwookshin.dock2

import android.app.Application

class App : Application() {
    companion object {
        lateinit var instance: App
        lateinit var auth: AuthRepo
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        auth = AuthRepo()
    }
}
