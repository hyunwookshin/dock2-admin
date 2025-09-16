// AuthRepo.kt  (replace startLogin + small changes)
package com.hyunwookshin.dock2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import net.openid.appauth.*

class AuthRepo {
    @Volatile private var idToken: String? = null
    @Volatile private var refreshToken: String? = null

    val hasToken get() = !idToken.isNullOrBlank()
    fun token() = idToken

    fun logout() {
        idToken = null
        refreshToken = null
        Prefs.forceReauth = true
    }

    fun startLogin(
        activity: Activity,
        onLaunched: () -> Unit,
        onError: (String) -> Unit
    ) {
        val domain = "us-east-2yx5yfkshr.auth.us-east-2.amazoncognito.com"

        // Hosted UI endpoints
        val cfg = AuthorizationServiceConfiguration(
            Uri.parse("https://$domain/login"),            // use /login
            Uri.parse("https://$domain/oauth2/token"),
            null,
            Uri.parse("https://$domain/logout")
        )

        // Force re-auth every time without using 'prompt=login'
        val extras = mapOf("max_age" to "0")               // <- key change

        val req = AuthorizationRequest.Builder(
            cfg,
            Oidc.CLIENT_ID,
            ResponseTypeValues.CODE,
            Oidc.REDIRECT_URI
        )
            .setScopes("openid", "email")
            // .setPrompt("login")                         // 🔕 remove this
            .setAdditionalParameters(extras)               // ✅ use max_age=0 instead
            .build()

        android.util.Log.d("Auth", "Authorize URL: ${req.toUri()}")

        val svc = AuthorizationService(activity)
        val intent = svc.getAuthorizationRequestIntent(req)
        if (intent.resolveActivity(activity.packageManager) == null) {
            onError("No browser found to handle sign-in."); return
        }
        onLaunched()
        activity.startActivityForResult(intent, 1001)
    }

    fun handleAuthResponse(activity: Activity, data: Intent?, onDone: (Boolean) -> Unit) {
        val resp = AuthorizationResponse.fromIntent(data ?: return onDone(false))
        val ex = AuthorizationException.fromIntent(data)
        Log.d("Auth", "Auth resp code=${resp?.authorizationCode} state=${resp?.state}")

        if (ex != null || resp == null) {
            Log.e("Auth", "Auth error: ${ex?.errorDescription ?: ex?.message}")
            return onDone(false)
        }

        val svc = AuthorizationService(activity)
        Log.d("Auth", "Exchanging code for tokens…")
        svc.performTokenRequest(resp.createTokenExchangeRequest()) { tokenResp, tokEx ->
            svc.dispose()
            if (tokEx != null || tokenResp == null) {
                Log.e("Auth", "Token exchange failed: ${tokEx?.errorDescription ?: tokEx?.error}")
                return@performTokenRequest onDone(false)
            }
            idToken = tokenResp.idToken
            refreshToken = tokenResp.refreshToken
            Prefs.forceReauth = false
            Log.d("Auth", "Token OK. id=${!idToken.isNullOrBlank()} refresh=${!refreshToken.isNullOrBlank()}")
            onDone(!idToken.isNullOrBlank())
        }
    }
}
