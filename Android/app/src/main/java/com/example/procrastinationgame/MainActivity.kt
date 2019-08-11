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
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private var mTimeSpent = 0
    private var isDebugMode = false
    private val TIME_SPENT_UNLOCKED_PHONE = "mTimeSpent"
    private val IS_DEBUG_MODE = "isDebugMode"
    private var mPreferences: SharedPreferences? = null

    private val sharedPrefFile = "com.example.android.procrstinationprefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        var a = 0
        fun checkLoop() {
            var handler = Handler()
            var waittTme = 1000 //One Second
            var idi = 0
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (isDebugMode) {
                        if (a >= 1) {
                            notify(switch1, idi)
                            idi++
                            a = 0
                        }
                        else {
                            a++
                        }
                    } else {
                        if (a >= 5) {
                            notify(switch1, idi)
                            idi++
                            a = 0
                        }
                        else {
                            a++
                        }
                    }
                    handler.postDelayed(this, waittTme.toLong())
                }
        }, waittTme.toLong())
    }
        checkLoop()

        openSettings()

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


    private fun openSettings() {
        startActivity(Intent(ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun getTime24Hours(usageStatsManager: UsageStatsManager): Int? {

        var beginTime = 0
        var endTime = 0
        var fullTime = 0

        var flag = false
        val INTERVAL = (24 * 60 * 60 * 1000).toLong()
        val end = System.currentTimeMillis()
        val begin = end - INTERVAL
        val usageEvents = usageStatsManager.queryEvents(begin, end)
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            println(1242)
            usageEvents.getNextEvent(event)
            if (!flag && event.eventType == UsageEvents.Event.SCREEN_INTERACTIVE) {
                beginTime = event.timeStamp.toInt()
                flag = true
            } else if (flag && event.eventType == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                endTime = event.timeStamp.toInt()
                flag = false
                fullTime += endTime - beginTime
            }
        }
        if (flag) {
            fullTime += System.currentTimeMillis().toInt() - beginTime
        }

        return fullTime
    }

    private fun getTimeDaily(usageStatsManager: UsageStatsManager): Int? {

        var beginTime = 0
        var endTime = 0
        var fullTime = 0

        var flag = false
        val INTERVAL = UsageStatsManager.INTERVAL_DAILY.toLong()
        var end = System.currentTimeMillis()
        var begin = endTime - INTERVAL

        val usageEvents = usageStatsManager.queryEvents(begin, end)
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            println(1242)
            usageEvents.getNextEvent(event)
            if (!flag && event.eventType == UsageEvents.Event.SCREEN_INTERACTIVE) {
                beginTime = event.timeStamp.toInt()
                flag = true
            } else if (flag && event.eventType == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                endTime = event.timeStamp.toInt()
                flag = false
                fullTime += endTime - beginTime
            }
        }
        if (flag) {
            fullTime += System.currentTimeMillis().toInt() - beginTime
        }

        return fullTime
    }



}

