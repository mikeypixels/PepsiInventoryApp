package com.example.michael.pepsiinventory.firebaseNotification

import android.content.Intent
import android.util.Log
import com.example.michael.pepsiinventory.AdminExpenseTableActivity
import com.example.michael.pepsiinventory.AdminSalesTableActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "MyFirebaseIdService"
    val TOPIC_GLOBAL = "global"
    val TITTLE = "title"
    val EMPTY = ""
    val MESSAGE = "message"
    val ACTION = "action"
    val DATA = "data"
    val ACTION_DESTINATION = "action_destination"

    companion object{
        var msg = ""
    }

    override fun onMessageReceived(message: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${message?.from}")

        // Check if message contains a data payload.
        message?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + message.data)
            val data = message.data
            handleData(data)

        }

        // Check if message contains a notification payload.
        message?.notification?.let {
            Log.d(TAG, "Message NotificationVO Body: ${it.body}")
            var notification = message.notification
            handleNotification(notification!!)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val builder = StringBuilder()
        builder.append("Refreshed Token: ")
            .append(token)
        Log.d(TAG, builder.toString())

//        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL)
//            .addOnCompleteListener { task ->
//                var msg = "Succesful subscribed to " + TOPIC_GLOBAL
//                if (!task.isSuccessful) {
//                    msg = "Failed to subscribe to " + TOPIC_GLOBAL
//                }
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }
    }

    private fun handleNotification(RemoteMsgNotification: RemoteMessage.Notification){
        val message: String = RemoteMsgNotification.body.toString()
        val title: String = RemoteMsgNotification.title.toString()
        val notificationVO = NotificationVO(title,message,"","")

        val resultIntent: Intent?
        resultIntent = if(title.contains("Sale")){
            Intent(applicationContext, AdminSalesTableActivity::class.java)
        }else{
            Intent(applicationContext, AdminExpenseTableActivity::class.java)
        }

        val notificationUtil = NotificationUtil(applicationContext)
        resultIntent.putExtra("message",message)
        msg = message
        notificationUtil.displayNotification(notificationVO, resultIntent)
//        notificationUtil.playNotificationSound()
    }

    private fun handleData(data: Map<String, String>){
        val title: String = data.get(TITTLE).toString()
        val message: String = data.get(MESSAGE).toString()
        val action: String = data.get(ACTION).toString()
        val actionDestination: String = data.get(ACTION_DESTINATION).toString()
        val notificationVO = NotificationVO(title,message,action,actionDestination)

        var resultIntent: Intent?
        resultIntent = if(title.contains("Sale")){
            Intent(applicationContext, AdminSalesTableActivity::class.java)
        }else{
            Intent(applicationContext, AdminExpenseTableActivity::class.java)
        }
        resultIntent.putExtra("message",message)
        msg = message
        val notificationUtil = NotificationUtil(applicationContext)
        notificationUtil.displayNotification(notificationVO, resultIntent)
//        notificationUtil.playNotificationSound()
    }
}
