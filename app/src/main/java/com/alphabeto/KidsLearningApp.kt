package com.alphabeto

import android.app.Application
import com.alphabeto.utils.SoundManager

class KidsLearningApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundManager.initialize(this)
    }
}
