package cz.muni.fi.pv239.boilercontroller.util

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

    companion object {
        private const val PREF_NAME = "prefs_boiler_controller"

        private const val USER_EMAIL = "user_email"
        private const val AUTH_TOKEN = "auth_token"
        private const val BOOST_CONFIG_TEMPERATURE = "boost_config_temperature"
        private const val BOOST_CONFIG_DURATION = "boost_config_duration"
    }

    private val shared: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var email: String?
        get() = shared.getString(USER_EMAIL, "")
        set(value) = shared.edit().putString(USER_EMAIL, value).apply()

    var token: String?
        get() = shared.getString(AUTH_TOKEN, "")
        set(value) = shared.edit().putString(AUTH_TOKEN, value).apply()

    var boostConfigTemperature: Int
        get() = shared.getInt(BOOST_CONFIG_TEMPERATURE, 0)
        set(value) = shared.edit().putInt(BOOST_CONFIG_TEMPERATURE, value).apply()

    var boostConfigDuration: Long
        get() = shared.getLong(BOOST_CONFIG_DURATION, 0)
        set(value) = shared.edit().putLong(BOOST_CONFIG_DURATION, value).apply()
}