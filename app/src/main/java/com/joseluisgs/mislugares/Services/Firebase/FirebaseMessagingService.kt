package com.joseluisgs.mislugares.Services.Firebase

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:  FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Looper.prepare()
        Handler().post {
            Toast.makeText(baseContext, remoteMessage.notification?.title,
                Toast.LENGTH_LONG)
                .show()
        }
        Looper.loop()
    }

}