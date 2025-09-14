package com.hyunwookshin.dock2

import android.app.Application


class App : Application() {
    companion object {
        lateinit var auth: AuthRepo
    }
    override fun onCreate() {
        super.onCreate()
        auth = AuthRepo()   // ← initialize once so it's never null/lateinit
    }
}
