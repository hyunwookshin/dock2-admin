package com.hyunwookshin.dock2

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import okhttp3.ResponseBody

data class ActionRequest(val action: String, val instanceId: String)

interface Ec2Service {
    @POST("dock2")
    suspend fun act(@Body req: ActionRequest): Response<ResponseBody>
}