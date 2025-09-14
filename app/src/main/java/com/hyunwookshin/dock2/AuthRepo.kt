package com.hyunwookshin.dock2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import net.openid.appauth.*

class AuthRepo {
    private var authService: AuthorizationService? = null
    @Volatile private var idToken: String? = null
    @Volatile private var refreshToken: String? = null

    val hasToken get() = !idToken.isNullOrBlank()
    fun token() = idToken

    fun startLogin(activity: Activity) {
        val cfg = AuthorizationServiceConfiguration(
            Uri.parse("https://${Oidc.DOMAIN}/oauth2/authorize"),
            Uri.parse("https://${Oidc.DOMAIN}/oauth2/token"),
            null,
            Uri.parse("https://${Oidc.DOMAIN}/logout")
        )

        authService?.dispose()
        authService = AuthorizationService(activity)

        val req = AuthorizationRequest.Builder(
            cfg,
            Oidc.CLIENT_ID,
            ResponseTypeValues.CODE,
            Oidc.REDIRECT_URI
        )
            .setScopes(*Oidc.SCOPES.toTypedArray())
            .build()

        val intent = authService!!.getAuthorizationRequestIntent(req)

        // ➊ Log what we’re doing
        android.util.Log.d("Auth", "Launching Hosted UI: ${req.toUri()}")

        // ➋ If a browser isn’t available, tell the user instead of hanging
        val canHandle = intent.resolveActivity(activity.packageManager) != null
        if (!canHandle) {
            android.widget.Toast.makeText(
                activity,
                "No browser found to open sign-in. Install or enable Chrome/Firefox.",
                android.widget.Toast.LENGTH_LONG
            ).show()
            return
        }

        // ➌ Launch (pairs with onActivityResult in LoginActivity)
        activity.startActivityForResult(intent, 1001)
    }


    fun handleAuthResponse(data: Intent?, onDone: (Boolean) -> Unit) {
        val resp = AuthorizationResponse.fromIntent(data ?: return onDone(false))
        val ex = AuthorizationException.fromIntent(data)

        android.util.Log.d("Auth", "Auth resp code=${resp?.authorizationCode} state=${resp?.state}")
        if (ex != null) {
            android.util.Log.e("Auth", "Auth error: code=${ex.code} type=${ex.type} error=${ex.errorDescription}")
            return onDone(false)
        }
        if (resp == null) {
            android.util.Log.e("Auth", "Auth resp is null")
            return onDone(false)
        }

        val svc = authService ?: run {
            android.util.Log.e("Auth", "AuthService was null during token exchange")
            return onDone(false)
        }

        // Exchange code for tokens
        val tokenReq = resp.createTokenExchangeRequest()
        android.util.Log.d("Auth", "Exchanging code for tokens…")
        svc.performTokenRequest(tokenReq) { tokenResp, tokEx ->
            if (tokEx != null) {
                android.util.Log.e("Auth", "Token exchange failed: ${tokEx.errorDescription ?: tokEx.error}")
                return@performTokenRequest onDone(false)
            }
            if (tokenResp == null) {
                android.util.Log.e("Auth", "Token response is null")
                return@performTokenRequest onDone(false)
            }
            idToken = tokenResp.idToken
            refreshToken = tokenResp.refreshToken
            android.util.Log.d("Auth", "Token OK. ID token present=${!idToken.isNullOrBlank()} refresh=${!refreshToken.isNullOrBlank()}")
            onDone(!idToken.isNullOrBlank())
        }
    }

}
