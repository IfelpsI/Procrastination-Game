package com.example.procrastinationgame

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.view.Window
import android.support.v4.os.HandlerCompat.postDelayed



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        /*Timer().scheduleAtFixedRate(object : TimerTask() {
            fun l() {
                notify(null)
            }
        }, 0, 1000)//put here time 1000 milliseconds=1 second*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "example1"
            val descriptionText = "example2"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("nc1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun notify(view: View) {
        var builder = NotificationCompat.Builder(this, "nc1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Stop starring at the Screen")
            .setContentText("You stare at screen too long")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("BUAH!!!"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(64, builder.build())
        }

    }
    fun loop(view: View) {
        val handler = Handler()
        val delay = 1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {
                notify(null)
                handler.postDelayed(this, delay)
            }
        }, delay)
    }

}



