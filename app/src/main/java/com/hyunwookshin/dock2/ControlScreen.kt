package com.hyunwookshin.dock2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ControlScreen(
    vm: ControlVm = viewModel(factory = ControlVm.Factory)
) {
    var instanceId by remember { mutableStateOf("i-0526c4d0519349f39") }
    val output by vm.result.collectAsStateWithLifecycle()

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
        }
    }
}
