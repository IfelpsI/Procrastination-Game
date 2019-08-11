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
        val UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var idi = 0
        var old = getTimeDaily(UsageStatsManager)
        fun checkLoop() {
            var handler = Handler()
            var waittTme = 1000 //One Second
            handler.postDelayed(object : Runnable {
                override fun run() {
                    var current = getTimeDaily(UsageStatsManager)
                    var delta = current - old
                    if (isDebugMode) {
                        if (delta >= 1000) {
                            notify(idi)
                            old = current
                            idi++
                        }
                    } else {
                        if (delta >= 20000) {
                            notify(idi)
                            old = current
                            idi++
                        }
                    }
                    handler.postDelayed(this, waittTme.toLong())
                }
        }, waittTme.toLong())
    }
        checkLoop() ///COMMENT THIS LINE TO PREVENT AUTOMATIC NOTIFICATIONS

        openSettings()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)

        mTimeSpent = mPreferences!!.getInt(TIME_SPENT_UNLOCKED_PHONE, 0)
        /*time_spent.text = mTimeSpent.toString()*/

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
        fun TestAlert() {
            notify(idi)
            idi++
        }
        button2.setOnClickListener {
            TestAlert()
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
    fun notify(identy: Int) {
        var builder = NotificationCompat.Builder(this, "nc1")
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setContentTitle("Notification Title")
            .setContentTitle(randomTitle())
            .setContentText("I am not Joking!!!")
            /*.setStyle(NotificationCompat.BigTextStyle()
                .bigText("BUAH!!!"))*/
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define

            notify(identy, builder.build())
        }
    }
    fun randomTitle(): String {
        var list: MutableList<String> = mutableListOf("Stop Using Your Phone","If You don't stop now, a kitten will die","Stop being addicted","PLEASE STOP USING YOUR PHONE!!!","Oh God Please NO, noooo!!!!")
        var randomElement = list.random()
        return randomElement
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

    private fun getTimeDaily(usageStatsManager: UsageStatsManager): Int {

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

