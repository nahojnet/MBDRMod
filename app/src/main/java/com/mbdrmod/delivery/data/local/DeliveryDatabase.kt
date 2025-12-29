package com.mbdrmod.delivery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mbdrmod.delivery.data.model.DeliveryData

@Database(
    entities = [DeliveryData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DeliveryDatabase : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao
}
