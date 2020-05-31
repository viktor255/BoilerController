package cz.muni.fi.pv239.boilercontroller.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.extension.toPresentableDate
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.splash.SplashActivity


class SyncNotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val temperatureConfigRepository by lazy { TemperatureConfigRepository(appContext) }

    override fun doWork(): Result {

        val currentDate = System.currentTimeMillis()
        temperatureConfigRepository.getCurrentTemperatureConfig { temperatureConfig ->
            temperatureConfig?.let {
                val lastSyncValue = it.time
                if (currentDate - lastSyncValue > 1000 * 60 * 15) {
                    val intent = Intent(appContext, SplashActivity::class.java)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

                    val builder = NotificationCompat.Builder(applicationContext, "normal")
                        .setSmallIcon(R.drawable.ic_whatshot_secondary_light_24dp)
                        .setContentTitle("Boiler Controller")
                        .setContentText("Boiler is offline, last sync at " + lastSyncValue.toPresentableDate())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    with(NotificationManagerCompat.from(appContext)) {
                        notify((lastSyncValue % 1000).toInt(), builder.build())
                    }
                }
            }
        }

        return Result.success()

    }
}