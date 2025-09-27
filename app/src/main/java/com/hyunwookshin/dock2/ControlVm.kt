package com.hyunwookshin.dock2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import org.json.JSONObject

private fun pretty(json: String): String = try {
    val state = JSONObject(json).optString("state")
    if (state.isBlank()) json else when (state.lowercase()) {
        "pending"         -> "Starting"
        "running"         -> "Running"
        "stopping"        -> "Stopping"
        "stopped"         -> "Stopped"
        "shutting-down"   -> "Shutting down"
        "terminated"      -> "Terminated"
        "rebooting"       -> "Rebooting"
        else              -> state.replaceFirstChar { it.titlecase() }
    }
} catch (_: Exception) {
    // If it isn't the expected JSON, just show whatever came back
    json
}

class ControlVm(
    private val service: Ec2Service = Net.api { App.auth.token() } // uses your ID token
) : ViewModel() {

    private val _result = MutableStateFlow("Ready")
    val result: StateFlow<String> = _result

    private suspend fun call(action: String, instanceId: String): Response<ResponseBody> =
        service.act(ActionRequest(action, instanceId))

    fun start(instanceId: String) = run("start", instanceId)
    fun stop(instanceId: String)  = run("stop",  instanceId)
    fun status(instanceId: String)= run("status",instanceId)

    private fun run(action: String, instanceId: String) {
        viewModelScope.launch {
            _result.value = "Calling $action..."
            runCatching { call(action, instanceId) }
                .onSuccess { r ->
                    _result.value = if (r.isSuccessful) {
                        pretty(r.body()?.string().orEmpty())
                    } else {
                        "HTTP ${r.code()} ${r.errorBody()?.string().orEmpty()}"
                    }
                }
                .onFailure { e -> _result.value = "Error: ${e.message}" }
        }
    }

    companion object {
        // Factory for use with viewModel(factory = ControlVm.Factory)
        val Factory = viewModelFactory {
            initializer { ControlVm() } // uses default service above
        }
    }
}
