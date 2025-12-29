package com.mbdrmod.delivery.util

import android.content.Context
import com.google.gson.Gson
import com.mbdrmod.delivery.data.model.AppConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cachedConfig: AppConfig? = null
    private val gson = Gson()

    fun getConfig(): AppConfig {
        cachedConfig?.let { return it }

        val config = try {
            val jsonString = context.assets.open("config.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, AppConfig::class.java)
        } catch (e: Exception) {
            AppConfig()
        }

        cachedConfig = config
        return config
    }

    fun reloadConfig() {
        cachedConfig = null
        getConfig()
    }
}
