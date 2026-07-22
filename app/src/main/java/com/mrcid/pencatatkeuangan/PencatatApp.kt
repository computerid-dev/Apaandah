package com.mrcid.pencatatkeuangan

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.mrcid.pencatatkeuangan.util.PrefManager

class PencatatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val pref = PrefManager(this)
        AppCompatDelegate.setDefaultNightMode(
            if (pref.darkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
