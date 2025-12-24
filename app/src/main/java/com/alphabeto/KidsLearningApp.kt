package com.alphabeto

import android.app.Application
import com.alphabeto.utils.SoundManager

class Alphabeto : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundManager.initialize(this)
    }
}
