package com.example.qlctncc_tn.fcm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.qlctncc_tn.MyApplication
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.activity.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //data Message
        val stringMap: Map<String, String> = remoteMessage.data
        val title = stringMap.get("user_name")
        val message = stringMap.get("description")
        sendNotification(title, message)
    }

    private fun sendNotification(title: String?, message: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentText(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
        val notification: Notification = notificationBuilder.build()
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            notificationManager.notify(1, notification)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("MyFirebaseMessagingService", token)
    }
}