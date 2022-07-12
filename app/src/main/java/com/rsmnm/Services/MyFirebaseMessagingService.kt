package com.rsmnm.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.rsmnm.Activities.DriverActivity
import com.rsmnm.Activities.PassengerActivity
import com.rsmnm.Activities.SplashActivity
import com.rsmnm.BuildConfig
import com.rsmnm.Models.NotifObject
import com.rsmnm.R
import java.util.*
import android.support.v4.content.ContextCompat.getSystemService
import com.rsmnm.Utils.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    val mContext = this;
    val CHANNEL_ID = "mnm_updates";

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)


        Log.e("PushNotification", remoteMessage.toString())
        try {
            if (remoteMessage?.getData() != null) {
                val gson = Gson()
                val notifJson = gson.toJson(remoteMessage?.getData())
                val notif = gson.fromJson(notifJson, NotifObject::class.java)
                sendNotif(notif.getTitle(), notif.getMessage(), notifJson)
                AppStore.getInstance().notificationLiveData.postValue(notif)

                if (notif.data_click_action == NotifObject.NotificationAction.marked_offline)
                    setUserItem(getUserItem().apply { this!!.is_online = 0 })
            }
            Log.e("FCM", "Notification Received : " + remoteMessage?.getData()?.get("data_click_action"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun sendNotif(title: String, msg: String, notifObject: String) {

        registerChanelID()

        StaticMethods.initPreferences(mContext)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)

        val intent: Intent
        if (getUserItem() != null) {
            if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER))
                intent = Intent(mContext, PassengerActivity::class.java)
            else
                intent = Intent(mContext, DriverActivity::class.java)
            intent.putExtra(AppConstants.KEY_NOTIF_DATA, notifObject)
        } else {
            intent = Intent(mContext, SplashActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(this, 1842, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.logo_white_login)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        builder.setContentTitle(title)
        builder.setContentText(msg)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(msg))
        builder.setAutoCancel(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random().nextInt(1000), builder.build())

    }

    fun registerChanelID() {
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = mContext.getString(R.string.app_name)
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}