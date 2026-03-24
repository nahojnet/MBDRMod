package com.mbdrmod.delivery.data.model

data class AppConfig(
    val webhook: WebhookConfig = WebhookConfig(),
    val requiredFields: RequiredFieldsConfig = RequiredFieldsConfig(),
    val deliverySites: List<String> = emptyList()
)

data class WebhookConfig(
    val url: String = "",
    val retryIntervalMinutes: Int = 15,
    val dataRetentionDays: Int = 30
)

data class RequiredFieldsConfig(
    val form1_livraison: Form1RequiredFields = Form1RequiredFields(),
    val form2_temperature: Form2RequiredFields = Form2RequiredFields(),
    val form3_supports: Form3RequiredFields = Form3RequiredFields(),
    val form4_collecte: Form4RequiredFields = Form4RequiredFields(),
    val form5_anomalies: Form5RequiredFields = Form5RequiredFields(),
    val form6_remarks: Form6RequiredFields = Form6RequiredFields(),
    val form7_finish: Form7RequiredFields = Form7RequiredFields()
)

data class Form1RequiredFields(
    val deliveryDate: Boolean = true,
    val clientNumber: Boolean = true,
    val tourNumber: Boolean = true,
    val expectedArrivalTime: Boolean = false,
    val actualArrivalTime: Boolean = false,
    val deliveryStartTime: Boolean = false,
    val supports: Boolean = false,
    val weightKg: Boolean = false,
    val packages: Boolean = false,
    val volumeM3: Boolean = false
)

data class Form2RequiredFields(
    val vehicleFrozen: Boolean = false,
    val vehicleFresh: Boolean = false,
    val productFrozen: Boolean = false,
    val productFrozenMethod: Boolean = false,
    val productFresh: Boolean = false,
    val productFreshMethod: Boolean = false
)

data class Form3RequiredFields(
    val doliesDelivered: Boolean = false,
    val doliesReturned: Boolean = false,
    val halfPalDelivered: Boolean = false,
    val halfPalReturned: Boolean = false,
    val europeDelivered: Boolean = false,
    val europeReturned: Boolean = false,
    val othersDelivered: Boolean = false,
    val othersReturned: Boolean = false,
    val panieresDelivered: Boolean = false,
    val panieresReturned: Boolean = false,
    val megabibDelivered: Boolean = false,
    val megabibReturned: Boolean = false
)

data class Form4RequiredFields(
    val plasticFilms: Boolean = false,
    val cardboard: Boolean = false,
    val aluminum: Boolean = false
)

data class Form5RequiredFields(
    val wrin: Boolean = false,
    val description: Boolean = false,
    val quantity: Boolean = false,
    val reason: Boolean = false
)

data class Form6RequiredFields(
    val remarks: Boolean = false
)

data class Form7RequiredFields(
    val deliveryEndTime: Boolean = false,
    val departureTime: Boolean = false,
    val driverName: Boolean = true,
    val managerName: Boolean = false
)
