package com.ivik.caseclose

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK
import android.provider.Settings
import android.provider.Settings.System.SCREEN_OFF_TIMEOUT
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class OnCaseClosedPowerOffService : Service(), SensorEventListener {
    lateinit var sensorManager: SensorManager
    lateinit var sensor: Sensor

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceIntent: PendingIntent = Intent(baseContext, MainActivity::class.java).let {
                notificationIntent -> PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val name = "Case Service"
        val descriptionText = "Turn off the device when case is closed"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel("caseCloseService", name, importance)
        channel.description = descriptionText

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, "caseCloseService")
            .setContentText("Turn off on case closed")
            .setContentText("turns off the device when the case is closed")
            .setSmallIcon(R.drawable.lock)
            .setContentIntent(serviceIntent)
            .build()

        startForeground(1337, notification)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isDeviceLocked) {
            return
        }


        if (event.values[0] == 0.0f) {
            val screenTimeout = Settings.System.getInt(contentResolver, SCREEN_OFF_TIMEOUT)
            Settings.System.putInt(contentResolver, SCREEN_OFF_TIMEOUT, 1000)
            Thread.sleep(1000)
            Settings.System.putInt(contentResolver, SCREEN_OFF_TIMEOUT, screenTimeout)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}