// LoginActivity.kt  (replace the whole file with this)
package com.hyunwookshin.dock2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class LoginActivity : ComponentActivity() {

    // Activity-owned state so we can flip it from callbacks and onActivityResult
    private val inProgress = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already signed in, go straight to app
        if (App.auth.hasToken) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            MaterialTheme {
                LoginScreen(
                    inProgress = inProgress.value,
                    onClick = {
                        if (!hasHttpHandler()) {
                            Toast.makeText(this, "No browser found. Install Chrome/Firefox.", Toast.LENGTH_LONG).show()
                            return@LoginScreen
                        }
                        // Do not flip spinner yet—only after we know launch will occur
                        App.auth.startLogin(
                            activity = this,
                            onLaunched = { inProgress.value = true },              // ✅ only spin when we actually launch
                            onError = { msg ->
                                inProgress.value = false                           // ✅ reset on any failure
                                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                )
            }
        }
    }

    // Back from Hosted UI
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001) {
            inProgress.value = false  // ✅ we’re back; stop spinner regardless

            App.auth.handleAuthResponse(this, data) { ok ->
                if (ok) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_LONG).show()
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

@Composable
private fun LoginScreen(inProgress: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "dock2", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))
            if (inProgress) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Opening sign-in…")
            } else {
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Sign in")
                }
            }
        }
    }


}
