// Net.kt (or its own file)
package com.hyunwookshin.dock2

import android.content.Intent
import android.os.Handler
import android.os.Looper
import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val resp = chain.proceed(chain.request())
        if (resp.code == 401) {
            // clear tokens
            App.auth.logout()
            // bounce to login on main thread
            Handler(Looper.getMainLooper()).post {
                val ctx = App.instance.applicationContext
                val i = Intent(ctx, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                ctx.startActivity(i)
            }
        }
        return resp
    }
}
