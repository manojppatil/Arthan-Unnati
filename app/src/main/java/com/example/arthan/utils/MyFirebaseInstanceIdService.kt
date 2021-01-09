package com.example.arthan.utils

import `in`.finbox.mobileriskmanager.notifications.MessagingService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.arthan.R
import com.example.arthan.views.activities.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    val TAG = "PushNotifService"
    lateinit var name: String

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        showNotification("received",(p0.data).get("message"),(p0.data).get("desc"))
        /**
         * In certain cases, FinBox server often requests critical data from SDK directly (other than scheduled sync period),
         * to make sure this works it is required to forward FCM Notifications to SDK.
         * Add the following lines inside the overridden onMessageReceived method available in the service that extends FirebaseMessagingService.
         */
      /*  if (MessagingService.forwardToFinBoxSDK(p0.data)) {
            val firebaseMessagingService = MessagingService()
            firebaseMessagingService.attachContext(this)
            firebaseMessagingService.onMessageReceived(p0)
        }*/
        //(p0.data).get("message")

    }
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "Token perangkat ini: $p0")
    }

    private fun showNotification(title: String?, message: String?, desc: String?) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message+"\n"+desc)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setPriority(Notification.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setChannelId("100")


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels(notificationManager)
        }
        notificationManager.notify(0, notificationBuilder.build())

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels(notificationManager: NotificationManager) {
        val adminChannelName = "arthan"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel("100", adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = "Notifications"
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }
}