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
                        r.body()?.string().orEmpty()
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
