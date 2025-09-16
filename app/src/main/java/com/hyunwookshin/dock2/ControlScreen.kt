package com.hyunwookshin.dock2

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ControlScreen(
    vm: ControlVm = viewModel(factory = ControlVm.Factory)
) {
    var instanceId by remember { mutableStateOf("i-0526c4d0519349f39") }
    val output by vm.result.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = instanceId,
                onValueChange = { instanceId = it },
                label = { Text("EC2 Instance ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.start(instanceId) }) { Text("Start") }
                Button(onClick = { vm.stop(instanceId)  }) { Text("Stop") }
                Button(onClick = { vm.status(instanceId)}) { Text("Status") }
            }
            Text("Result:")
            Text(output)
            Spacer(Modifier.height(24.dp))

            // Sign out button
            Button(
                onClick = {
                    App.auth.logout()  // clears tokens + sets forceReauthOnce
                    val i = Intent(ctx, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    ctx.startActivity(i)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Sign out")
            }

        }
    }
}
