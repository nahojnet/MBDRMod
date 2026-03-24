package com.mbdrmod.delivery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mbdrmod.delivery.data.model.DeliveryData

@Database(
    entities = [DeliveryData::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DeliveryDatabase : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE deliveries ADD COLUMN deliverySite TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE deliveries ADD COLUMN ssccNumbers TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
