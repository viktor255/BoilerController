package cz.muni.fi.pv239.boilercontroller.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.list.ListActivity
import cz.muni.fi.pv239.boilercontroller.ui.login.LoginActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager

class SplashActivity : AppCompatActivity() {

    private val prefManager: PrefManager? by lazy { PrefManager(this) }
    private val temperatureConfigRepository by lazy { TemperatureConfigRepository(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = prefManager?.email
        val password = prefManager?.password

        if (email != null && password != null) {
            temperatureConfigRepository.signIn(email, password) { user ->
                user?.let {
                    Log.d("USER", user.email)
                    prefManager?.token = user.token
                    prefManager?.email = user.email
                    temperatureConfigRepository.getBoostConfig {
                        it?.let {
                            prefManager?.boostConfigTemperature = it.temperature
                            prefManager?.boostConfigDuration = it.duration
                            prefManager?.boostConfigId = it._id
                            Log.d("Boost-get1", it.toString())
                        }
                    }
                    routeToAppropriatePage(true)
                    finish()
                }
            }
        } else {
            routeToAppropriatePage(false)
            finish()
        }
    }

    private fun routeToAppropriatePage(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            startActivity(ListActivity.newIntent(this))
        } else {
            startActivity(LoginActivity.newIntent(this))
        }
    }

}