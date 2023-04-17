package com.wedoapps.cricketLiveLine.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.ui.MainActivity
import java.util.*

class FirebaseMessagingServiceId : FirebaseMessagingService() {
    var mNotificationManager: NotificationManager? = null

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Constants.FIRE_BASE_TOKEN = p0
      /*  val intent = Intent(applicationContext,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(AppConstants.FIRE_BASE_NEW_TOKEN,p0)
        startActivity(intent)*/

    }
    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val isBackGround: String? = remoteMessage.data["is_background"]
        val title: String? = remoteMessage.data["title"]
        val message: String? = remoteMessage.data["message"]
        val type: String? = remoteMessage.data["type"]
        val timeStamp: String? = remoteMessage.data["timestamp"]
        val sound: String? = remoteMessage.data["sound"]
        val vibrate: String? = remoteMessage.data["vibrate"]
        val imageUrl: String? = remoteMessage.data["image"].toString()

        println("FCM IMAGE-->"+imageUrl.toString())

        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.isLooping = false
        }

        // vibration
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))

        // Get the layouts to use in the custom notification
        val channelId = "T-20"
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)
        val builder = NotificationCompat.Builder(applicationContext, channelId)
        builder.setSmallIcon(R.mipmap.ic_notification)
        builder.setLargeIcon(
            BitmapFactory.decodeResource(
                this.resources,
                R.mipmap.ic_notification
            )
        )
        val resultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT or  PendingIntent.FLAG_IMMUTABLE)
        /*  if (remoteMessage.data.containsKey("bigPicture")) {
            remoteMessage.data["bigPicture"]
        }*/
        notificationLayout.setTextViewText(
            R.id.txtNotificationTitle,
            Objects.requireNonNull(remoteMessage.notification)!!.title
        )
        notificationLayout.setTextViewText(
            R.id.txtNotificationTitle,
            Objects.requireNonNull(remoteMessage.notification)!!.body
        )
        notificationLayoutExpanded.setTextViewText(
            R.id.txtNotificationTitle,
            Objects.requireNonNull(remoteMessage.notification)!!.title
        )
        notificationLayoutExpanded.setTextViewText(
            R.id.txtNotificationTitle,
            Objects.requireNonNull(remoteMessage.notification)!!.body
        )
        notificationLayoutExpanded.setImageViewUri(
            R.id.imgNotification,
            Objects.requireNonNull(remoteMessage.notification)!!.imageUrl
        )

        builder.setContentTitle(remoteMessage.notification!!.title)
        builder.setContentText(remoteMessage.notification!!.body)

        builder.setCustomContentView(notificationLayout)
        builder.setCustomBigContentView(notificationLayoutExpanded)
        builder.setContentIntent(pendingIntent)
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(remoteMessage.notification!!.body)
        )
        builder.setAutoCancel(true)
        builder.priority = Notification.VISIBILITY_PUBLIC
        mNotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//          val channelId = "MatchLine"
            val channelName="Notify Score"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = remoteMessage.notification!!.body
            channel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            channel.lightColor = Color.BLUE

            mNotificationManager!!.createNotificationChannel(channel)
            builder.setChannelId(channelId)

        }

        // notificationId is a unique int for each notification that you must define
        mNotificationManager!!.notify(100, builder.build())
    }

}