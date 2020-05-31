package cz.muni.fi.pv239.boilercontroller.repository

import android.content.Context
import androidx.preference.PreferenceDataStore
import cz.muni.fi.pv239.boilercontroller.model.BoostConfig
import cz.muni.fi.pv239.boilercontroller.util.PrefManager

class CustomPreferenceDataStore(context: Context?) : PreferenceDataStore() {
    private lateinit var boostConfig: BoostConfig
    private val apiRepository by lazy { context?.let { WebAPIRepository(it) } }
    private val prefManager: PrefManager? by lazy { context?.let { PrefManager(it) } }

    override fun putString(key: String, value: String?) {
        // Save the value somewhere
        value?.let {
            if (key == "boostTemperature") {
                boostConfig = boostConfig.copy(temperature = value.toInt())
                prefManager?.boostConfigTemperature = value.toInt()
            }
            if (key == "boostDuration") {
                boostConfig = boostConfig.copy(duration = value.toLong())
                prefManager?.boostConfigDuration = value.toLong()
            }
            apiRepository?.updateBoostConfig(boostConfig) {
                it?.let {
                    prefManager?.boostConfigTemperature = it.temperature
                    prefManager?.boostConfigDuration = it.duration
                    prefManager?.boostConfigId = it._id
                }
            }
        }
    }

    override fun getString(key: String, defValue: String?): String? {
        // Retrieve the value
        apiRepository?.getBoostConfig {
            it?.let {
                boostConfig = BoostConfig(it._id, it.duration, it.temperature)
                prefManager?.boostConfigTemperature = it.temperature
                prefManager?.boostConfigDuration = it.duration
                prefManager?.boostConfigId = it._id
            }
        }

        if (key == "boostTemperature") {
            return prefManager?.boostConfigTemperature.toString()
        }
        if (key == "boostDuration") {
            return prefManager?.boostConfigDuration.toString()
        }
        return "unknown"
    }
}