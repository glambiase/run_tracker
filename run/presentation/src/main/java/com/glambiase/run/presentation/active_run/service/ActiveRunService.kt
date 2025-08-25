package com.glambiase.run.presentation.active_run.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.glambiase.core.presentation.ui.formatted
import com.glambiase.run.domain.RunningTracker
import com.glambiase.run.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class ActiveRunService : Service() {

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(com.glambiase.core.presentation.designsystem.R.drawable.logo)
            .setContentTitle(getString(R.string.notification_title))
            .setOnlyAlertOnce(true)
    }

    private val runningTracker by inject<RunningTracker>()

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS).orEmpty()
                start(Class.forName(activityClass))
            }
            ACTION_STOP -> {
                stop()
            }
        }
        return START_STICKY
    }

    private fun start(activeClass: Class<*>) {
        if (!isServiceActive) {
            isServiceActive = true

            createNotificationChannel()

            val activityIntent = Intent(applicationContext, activeClass).apply {
                data = "run_tracker://active_run".toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build()

            startForeground(NOTIFICATION_ID, notification)
            updateNotification()
        }
    }

    private fun stop() {
        stopSelf()
        isServiceActive = false
        serviceScope.coroutineContext.cancelChildren()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_title),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        runningTracker.elapsedTime.onEach { elapsedTime ->
            val notification = baseNotification
                .setContentText(elapsedTime.formatted())
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }.launchIn(serviceScope)
    }

    companion object {
        private const val CHANNEL_ID = "active_run"
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"
        private const val NOTIFICATION_ID = 1

        var isServiceActive = false

        fun createStartIntent(context: Context, activityClass: Class<*>) =
            Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }

        fun createStopIntent(context: Context) =
            Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
    }
}