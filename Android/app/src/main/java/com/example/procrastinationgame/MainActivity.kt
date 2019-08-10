package com.example.procrastinationgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast


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
        }
    }

    override fun onPause() {
        super.onPause()

        val preferencesEditor = mPreferences!!.edit()
        preferencesEditor.putInt(TIME_SPENT_UNLOCKED_PHONE, mTimeSpent)
        preferencesEditor.putBoolean(IS_DEBUG_MODE, isDebugMode)
        preferencesEditor.apply()
    }

}

