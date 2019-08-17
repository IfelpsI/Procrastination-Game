package com.example.procrastinationgame

import android.app.Application
import android.widget.Toast
import com.vk.sdk.VKSdk


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(applicationContext)
        Toast.makeText(this, "kek", Toast.LENGTH_LONG).show()
    }
}