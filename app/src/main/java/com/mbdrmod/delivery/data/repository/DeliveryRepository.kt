package com.mbdrmod.delivery.data.repository

import com.mbdrmod.delivery.data.local.DeliveryDao
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.SendStatus
import com.mbdrmod.delivery.data.model.toWebhookPayload
import com.mbdrmod.delivery.data.remote.WebhookService
import com.mbdrmod.delivery.util.ConfigManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepository @Inject constructor(
    private val deliveryDao: DeliveryDao,
    private val webhookService: WebhookService,
    private val configManager: ConfigManager
) {
    fun getAllDeliveries(): Flow<List<DeliveryData>> {
        return deliveryDao.getAllDeliveries()
    }

    suspend fun getDeliveryById(id: Long): DeliveryData? {
        return deliveryDao.getDeliveryById(id)
    }

    suspend fun saveDelivery(delivery: DeliveryData): Long {
        return deliveryDao.insertDelivery(delivery)
    }

    suspend fun updateDelivery(delivery: DeliveryData) {
        deliveryDao.updateDelivery(delivery)
    }

    suspend fun deleteDelivery(id: Long) {
        deliveryDao.deleteDeliveryById(id)
    }

    suspend fun sendDelivery(delivery: DeliveryData): Result<Unit> {
        val config = configManager.getConfig()
        val webhookUrl = config.webhook.url

        if (webhookUrl.isEmpty()) {
            return Result.failure(Exception("Webhook URL not configured"))
        }

        return try {
            val payload = delivery.toWebhookPayload()
            val response = webhookService.sendDeliveryData(webhookUrl, payload)

            if (response.isSuccessful) {
                deliveryDao.markAsSent(delivery.id, System.currentTimeMillis())
                Result.success(Unit)
            } else {
                deliveryDao.updateSendStatus(
                    delivery.id,
                    SendStatus.FAILED,
                    System.currentTimeMillis()
                )
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            deliveryDao.updateSendStatus(
                delivery.id,
                SendStatus.FAILED,
                System.currentTimeMillis()
            )
            Result.failure(e)
        }
    }

    suspend fun getPendingDeliveries(): List<DeliveryData> {
        return deliveryDao.getPendingDeliveries()
    }

    suspend fun cleanupOldDeliveries() {
        val config = configManager.getConfig()
        val retentionMillis = config.webhook.dataRetentionDays * 24 * 60 * 60 * 1000L
        val cutoffTime = System.currentTimeMillis() - retentionMillis

        val oldDeliveries = deliveryDao.getDeliveriesOlderThan(cutoffTime)
        oldDeliveries.filter { it.sendStatus == SendStatus.SENT }.forEach {
            deliveryDao.deleteDelivery(it)
        }
    }
}
