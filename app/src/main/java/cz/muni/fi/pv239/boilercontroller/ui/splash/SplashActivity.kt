package cz.muni.fi.pv239.boilercontroller.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.repository.WebAPIRepository
import cz.muni.fi.pv239.boilercontroller.ui.main.MainActivity
import cz.muni.fi.pv239.boilercontroller.ui.login.LoginActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import cz.muni.fi.pv239.boilercontroller.util.SyncNotificationWorker
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private val prefManager: PrefManager? by lazy { PrefManager(this) }
    private val apiRepository by lazy { WebAPIRepository(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setNotificationWorkerForLastSync()

        val email = prefManager?.email
        val password = prefManager?.password

        if (email != null && password != null) {
            apiRepository.signIn(email, password) { user ->
                user?.let {
                    Log.d("USER", user.email)
                    prefManager?.token = user.token
                    prefManager?.email = user.email
                    apiRepository.getBoostConfig {
                        it?.let {
                            prefManager?.boostConfigTemperature = it.temperature
                            prefManager?.boostConfigDuration = it.duration
                            prefManager?.boostConfigId = it._id
                            Log.d("Boost-get1", it.toString())
                        }
                    }
                    routeToAppropriatePage(true)
                    finish()
                } ?: kotlin.run {
                    routeToAppropriatePage(false)
                    val toast = Toast.makeText(applicationContext, "You are offline", Toast.LENGTH_LONG)
                    toast.show()
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
            startActivity(MainActivity.newIntent(this))
        } else {
            startActivity(LoginActivity.newIntent(this))
        }
    }

    private fun setNotificationWorkerForLastSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<SyncNotificationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("lastSyncWorker", ExistingPeriodicWorkPolicy.KEEP, work)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("normal", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}