package com.mbdrmod.delivery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mbdrmod.delivery.data.local.Converters

@Entity(tableName = "deliveries")
@TypeConverters(Converters::class)
data class DeliveryData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Form 1 - Livraison - Client
    val deliveryDate: String = "",
    val clientNumber: String = "",
    val tourNumber: Int? = null,

    // Form 1 - Livraison - Horaire
    val expectedArrivalTime: String = "",
    val actualArrivalTime: String = "",
    val deliveryStartTime: String = "",

    // Form 1 - Livraison - Quantité
    val supports: Int? = null,
    val weightKg: Double? = null,
    val packages: Int? = null,
    val volumeM3: Double? = null,

    // Form 2 - Température - Véhicule
    val vehicleFrozenTemp: Double? = null,
    val vehicleFreshTemp: Double? = null,

    // Form 2 - Température - Produits
    val productFrozenTemp: Double? = null,
    val productFrozenMethod: String = "", // "inertia_probe" or "manual_control"
    val productFreshTemp: Double? = null,
    val productFreshMethod: String = "", // "inertia_probe" or "manual_control"

    // Form 3 - Supports de livraison
    val doliesDelivered: Int? = null,
    val doliesReturned: Int? = null,
    val halfPalDelivered: Int? = null,
    val halfPalReturned: Int? = null,
    val europeDelivered: Int? = null,
    val europeReturned: Int? = null,
    val othersDelivered: Int? = null,
    val othersReturned: Int? = null,
    val panieresDelivered: Int? = null,
    val panieresReturned: Int? = null,
    val megabibDelivered: Int? = null,
    val megabibReturned: Int? = null,

    // Form 4 - Collecte
    val plasticFilms: Int? = null,
    val cardboard: Int? = null,
    val aluminum: Int? = null,

    // Form 5 - Anomalies (stored as JSON)
    val anomalies: List<Anomaly> = emptyList(),

    // Form 6 - Remarques
    val remarks: String = "",

    // Form 7 - Terminer
    val deliveryEndTime: String = "",
    val departureTime: String = "",
    val driverName: String = "",
    val managerName: String = "",

    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val sendStatus: SendStatus = SendStatus.PENDING,
    val lastSendAttempt: Long? = null,
    val sentAt: Long? = null
)

data class Anomaly(
    val id: Long = System.currentTimeMillis(),
    val wrin: String = "",
    val description: String = "",
    val quantity: Int? = null,
    val missing: Int? = null,
    val refused: Int? = null,
    val excess: Int? = null,
    val reason: String = ""
)

enum class SendStatus {
    PENDING,
    SENT,
    FAILED
}
