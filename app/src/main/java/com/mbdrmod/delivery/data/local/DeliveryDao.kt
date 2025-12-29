package com.mbdrmod.delivery.data.local

import androidx.room.*
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.SendStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {
    @Query("SELECT * FROM deliveries ORDER BY createdAt DESC")
    fun getAllDeliveries(): Flow<List<DeliveryData>>

    @Query("SELECT * FROM deliveries WHERE id = :id")
    suspend fun getDeliveryById(id: Long): DeliveryData?

    @Query("SELECT * FROM deliveries WHERE sendStatus = :status")
    suspend fun getDeliveriesByStatus(status: SendStatus): List<DeliveryData>

    @Query("SELECT * FROM deliveries WHERE sendStatus = 'PENDING' OR sendStatus = 'FAILED'")
    suspend fun getPendingDeliveries(): List<DeliveryData>

    @Query("SELECT * FROM deliveries WHERE createdAt < :timestamp")
    suspend fun getDeliveriesOlderThan(timestamp: Long): List<DeliveryData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryData): Long

    @Update
    suspend fun updateDelivery(delivery: DeliveryData)

    @Delete
    suspend fun deleteDelivery(delivery: DeliveryData)

    @Query("DELETE FROM deliveries WHERE id = :id")
    suspend fun deleteDeliveryById(id: Long)

    @Query("UPDATE deliveries SET sendStatus = :status, lastSendAttempt = :timestamp WHERE id = :id")
    suspend fun updateSendStatus(id: Long, status: SendStatus, timestamp: Long)

    @Query("UPDATE deliveries SET sendStatus = 'SENT', sentAt = :timestamp WHERE id = :id")
    suspend fun markAsSent(id: Long, timestamp: Long)
}
