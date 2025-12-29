package com.mbdrmod.delivery.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mbdrmod.delivery.data.model.Anomaly
import com.mbdrmod.delivery.data.model.SendStatus

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAnomalyList(anomalies: List<Anomaly>): String {
        return gson.toJson(anomalies)
    }

    @TypeConverter
    fun toAnomalyList(json: String): List<Anomaly> {
        if (json.isEmpty()) return emptyList()
        val type = object : TypeToken<List<Anomaly>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromSendStatus(status: SendStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSendStatus(value: String): SendStatus {
        return SendStatus.valueOf(value)
    }
}
