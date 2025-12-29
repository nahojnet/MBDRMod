package com.mbdrmod.delivery.data.remote

import com.mbdrmod.delivery.data.model.WebhookPayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface WebhookService {
    @POST
    suspend fun sendDeliveryData(
        @Url url: String,
        @Body payload: WebhookPayload
    ): Response<Unit>
}
