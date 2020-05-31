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
import cz.muni.fi.pv239.boilercontroller.ui.splash.SplashActivity


class BoostNotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val boostStart = inputData.getLong("boost-start", 0)
        val boostDuration = inputData.getLong("boost-duration", 0)
        val boostEnd = boostStart + boostDuration * 1000 * 60

        val intent = Intent(appContext, SplashActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, "normal")
            .setSmallIcon(R.drawable.ic_whatshot_secondary_light_24dp)
            .setContentTitle("Boiler Controller")
            .setContentText("Boost have ended at " + boostEnd.toPresentableDate())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(appContext)) {
            notify((boostEnd % 1000).toInt(), builder.build())
        }

        return Result.success()

    }
}