// Prefs.kt
package com.hyunwookshin.dock2

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val FILE = "dock2_prefs"
    private const val KEY_FORCE_REAUTH = "force_reauth"
    private const val KEY_AUTH_IN_PROGRESS = "auth_in_progress"

    private fun prefs(): SharedPreferences =
        App.instance.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    var forceReauth: Boolean
        get() = prefs().getBoolean(KEY_FORCE_REAUTH, false)
        set(v) { prefs().edit().putBoolean(KEY_FORCE_REAUTH, v).apply() }

    var authInProgress: Boolean
        get() = prefs().getBoolean(KEY_AUTH_IN_PROGRESS, false)
        set(v) { prefs().edit().putBoolean(KEY_AUTH_IN_PROGRESS, v).apply() }


    private const val KEY_POST_LOGOUT_LOGIN_ONCE = "post_logout_login_once"

    var postLogoutLoginOnce: Boolean
        get() = prefs().getBoolean(KEY_POST_LOGOUT_LOGIN_ONCE, false)
        set(v) { prefs().edit().putBoolean(KEY_POST_LOGOUT_LOGIN_ONCE, v).apply() }
}
