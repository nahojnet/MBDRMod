package com.mbdrmod.delivery.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mbdrmod.delivery.data.repository.DeliveryRepository
import com.mbdrmod.delivery.util.ConfigManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WebhookRetryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: DeliveryRepository,
    private val configManager: ConfigManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pendingDeliveries = repository.getPendingDeliveries()

        var allSuccessful = true
        for (delivery in pendingDeliveries) {
            val result = repository.sendDelivery(delivery)
            if (result.isFailure) {
                allSuccessful = false
            }
        }

        // Also cleanup old deliveries
        repository.cleanupOldDeliveries()

        return if (allSuccessful) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "webhook_retry_work"

        fun schedulePeriodicWork(context: Context, intervalMinutes: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<WebhookRetryWorker>(
                intervalMinutes.toLong(),
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun scheduleOneTimeWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<WebhookRetryWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
