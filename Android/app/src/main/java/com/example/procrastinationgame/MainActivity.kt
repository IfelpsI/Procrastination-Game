package com.example.procrastinationgame

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mTimeSpent = 0
    private var isDebugMode = false
    private val TIME_SPENT_UNLOCKED_PHONE = "mTimeSpent"
    private val IS_DEBUG_MODE = "isDebugMode"
    private var mPreferences: SharedPreferences? = null

    private val sharedPrefFile = "com.example.android.procrstinationprefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)

        mTimeSpent = mPreferences!!.getInt(TIME_SPENT_UNLOCKED_PHONE, 0)
        time_spent.text = mTimeSpent.toString()

        isDebugMode = mPreferences!!.getBoolean(IS_DEBUG_MODE, false)

        if (isDebugMode) {
            switch1.isChecked = true
        }

        switch1.setOnClickListener {
            isDebugMode = switch1.isChecked
            if (switch1.isChecked) {
                val text = resources.getString(R.string.switched)
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            } else {
                val text = resources.getString(R.string.not_switched)
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
            luup(switch1)
        }
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

    override fun onPause() {
        super.onPause()

        val preferencesEditor = mPreferences!!.edit()
        preferencesEditor.putInt(TIME_SPENT_UNLOCKED_PHONE, mTimeSpent)
        preferencesEditor.putBoolean(IS_DEBUG_MODE, isDebugMode)
        preferencesEditor.apply()
    }

    fun notify(view: View,identy: Int) {
        var builder = NotificationCompat.Builder(this, "nc1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notification Title")
            .setContentText("Notification Text")
            /*.setStyle(NotificationCompat.BigTextStyle()
                .bigText("BUAH!!!"))*/
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define

            notify(identy, builder.build())
        }
    }

    fun luup(view: View) {
        val handler = Handler()
         //milliseconds
        var NIdenty = 0
        var delay = 0
        handler.postDelayed(object : Runnable {
            override fun run() {
                notify(view,NIdenty)
                NIdenty = NIdenty + 1
                if (isDebugMode) {
                    delay = 100
                }
                else {
                    delay = 10000
                }
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }
}

