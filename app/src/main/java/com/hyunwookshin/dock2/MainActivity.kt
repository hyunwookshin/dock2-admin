package com.hyunwookshin.dock2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!App.auth.hasToken) {            // hard gate
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        setContent { ControlScreen() }  // VM-based version
    }
}
