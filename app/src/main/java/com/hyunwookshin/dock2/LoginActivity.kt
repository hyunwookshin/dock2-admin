package com.hyunwookshin.dock2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Small visible placeholder so it's never blank
        val tv = TextView(this).apply {
            text = "Opening sign-in…"
            textSize = 18f
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(FrameLayout(this).apply { addView(tv) })

        // Already logged in? Go straight to app.
        if (App.auth.hasToken) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Ensure there is a real browser available (prevents self-loop)
        if (!hasHttpHandler()) {
            Toast.makeText(this, "No browser found. Please install or enable Chrome/Firefox.", Toast.LENGTH_LONG).show()
            runCatching {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.android.chrome")))
            }
            return
        }

        android.util.Log.d("Auth", "LoginActivity: starting Hosted UI")
        // Start the Hosted UI through AuthRepo (so the same AuthorizationService is used later)
        App.auth.startLogin(this)

    }

    // Classic onActivityResult pairs with startActivityForResult in AuthRepo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001) {
            App.auth.handleAuthResponse(data) { ok ->
                if (ok) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Sign-in failed. Check browser & callback URL.", Toast.LENGTH_LONG).show()
                    // Avoid immediate loop; let the user relaunch the app or tap a "Sign in" button if you add one.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun hasHttpHandler(): Boolean {
        val test = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
        return packageManager.resolveActivity(test, 0) != null
    }
}
