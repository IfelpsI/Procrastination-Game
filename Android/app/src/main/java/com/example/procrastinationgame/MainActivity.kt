package com.example.procrastinationgame

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
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
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.content.Intent
import android.content.pm.ApplicationInfo
import java.util.Date
import android.content.pm.PackageInfo


import android.media.MediaPlayer
import android.net.Uri
import java.nio.file.Paths
import android.media.AudioAttributes
import android.util.Log
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import com.vk.sdk.util.VKUtil
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var mTimeSpent = 0
    private var isDebugMode = false
    private val TIME_SPENT_UNLOCKED_PHONE = "mTimeSpent"
    private val IS_DEBUG_MODE = "isDebugMode"
    private var mPreferences: SharedPreferences? = null

    private val sharedPrefFile = "com.example.android.procrstinationprefs"

    override fun onCreate(savedInstanceState: Bundle?) {

        fun checkForPermission(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            return mode == MODE_ALLOWED
        }


        var installedApps = getInstalledApps()

        val UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        if (!checkForPermission(this)) {
            openSettings()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //var files = listOf("breaking-some-glass", "filling-your-inbox", "slow-spring-board")
        //var rdomF = files.random()

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)

        mTimeSpent = mPreferences!!.getInt(TIME_SPENT_UNLOCKED_PHONE, 0)
        /*time_spent.text = mTimeSpent.toString()*/

        isDebugMode = mPreferences!!.getBoolean(IS_DEBUG_MODE, false)

        val fingerprints = VKUtil.getCertificateFingerprint(this, this.packageName);
        Log.e(MainActivity::class.java.simpleName, "kekeke ${fingerprints[0]}")

        button2.setOnClickListener {
            VKSdk.login(this, VKScope.FRIENDS)

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
            val audioAttributes = AudioAttributes.Builder()
                //.setContentType(AudioAttributes.USAGE_ALARM)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            //var soundUri = Uri.parse("android.resource://com.example.procrastinationgame/" + R.raw.breaking_some_glass)
            //channel.setSound(soundUri, audioAttributes)
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
            //.setSound(Uri.parse("android.resource://com.example.procrastinationgame/" + R.raw.filling_your_inbox))
            //notMP.start()
            /*.setStyle(NotificationCompat.BigTextStyle()
                .bigText("BUAH!!!"))*/
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define

            notify(identy, builder.build())
        }
    }

    fun randomTitle(): String {
        var list = mutableListOf(
            "Stop Using Your Phone",
            "If You don't stop now, a kitten will die",
            "Stop being addicted",
            "PLEASE STOP USING YOUR PHONE!!!",
            "Oh God Please NO, noooo!!!!"
        )
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

        val curDate = Date()
        val curMillis = (curDate.year - 71) * curDate.month * curDate.date * 24 * 60 * 60 * 1000

        var flag = false
        val end = System.currentTimeMillis()
        val begin = curMillis.toLong()

        val usageEvents = usageStatsManager.queryEvents(begin, end)
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
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

    private fun getTimeMonthly(usageStatsManager: UsageStatsManager): Int? {

        var beginTime = 0
        var endTime = 0
        var fullTime = 0

        var flag = false
        val INTERVAL = (2.628 * 1000000000).toLong()
        val end = System.currentTimeMillis()
        val begin = end - INTERVAL

        val usageEvents = usageStatsManager.queryEvents(begin, end)
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
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

    private fun getTimeAppsUsed24Hours(
        usageStatsManager: UsageStatsManager,
        installedApps: List<AppList>
    ): List<AppList> {

        val packages: MutableList<String> = mutableListOf("string")
        var fullPackages: List<String> = packages
        var Package = ""
        val time: MutableList<Int> = mutableListOf()
        var fullTime: List<Int> = time

        var flag = false
        val INTERVAL = (24 * 60 * 60 * 1000).toLong()
        val end = System.currentTimeMillis()
        val begin = end - INTERVAL

        val usageEvents = usageStatsManager.queryEvents(begin, end)
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            var beginTime = 0
            var endTime: Int
            usageEvents.getNextEvent(event)
            if (!flag && event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                packages.add(event.packageName)
                Package = event.packageName
                beginTime = event.timeStamp.toInt()
                flag = true
            } else if (flag && event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                endTime = event.timeStamp.toInt()
                time.add(endTime - beginTime)
                for (i in installedApps) {
                    if (i.packageName == Package) {
                        i.time += endTime - beginTime
                    }
                }
                flag = false
            }
        }
        return installedApps
    }

    private fun getInstalledApps(): List<AppList> {
        val res = ArrayList<AppList>()
        val packs = packageManager.getInstalledPackages(0)
        for (i in packs.indices) {
            val p = packs[i]
            if (isSystemPackage(p) === false) {
                val appName = p.applicationInfo.loadLabel(packageManager).toString()
                val appPackageName = p.packageName
                res.add(AppList(appName, appPackageName))
            }
        }
        return res
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

}
