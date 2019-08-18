package com.example.procrastinationgame

import android.app.Application
import android.content.Intent
import android.widget.Toast
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKSdk


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(applicationContext)
    }
}