package com.alphabeto.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

object SoundManager {
    private const val MAX_STREAMS = 4

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val loadedSounds: MutableMap<Int, Int> = ConcurrentHashMap()
    private val loadingSounds: MutableMap<Int, CompletableDeferred<Int>> = ConcurrentHashMap()
    private val sampleToResId: MutableMap<Int, Int> = ConcurrentHashMap()

    @Volatile
    private var soundPool: SoundPool? = null
    private var appContext: Context? = null

    fun initialize(context: Context) {
        if (soundPool != null) return
        synchronized(this) {
            if (soundPool != null) return
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()

            val pool = SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()
            pool.setOnLoadCompleteListener { _, sampleId, status ->
                val resId = sampleToResId.remove(sampleId)
                val deferred = resId?.let { loadingSounds.remove(it) }
                if (status == 0 && resId != null) {
                    loadedSounds[resId] = sampleId
                    deferred?.complete(sampleId)
                } else {
                    deferred?.completeExceptionally(IllegalStateException("Failed to load sound $sampleId"))
                }
            }
            appContext = context.applicationContext
            soundPool = pool
        }
    }

    fun play(@RawRes resId: Int) {
        if (resId == 0) return
        val pool = soundPool ?: return
        loadedSounds[resId]?.let { sampleId ->
            pool.play(sampleId, 1f, 1f, 1, 0, 1f)
            return
        }

        val context = appContext ?: return
        val deferred = loadingSounds.getOrPut(resId) { CompletableDeferred() }
        if (!sampleToResId.containsValue(resId)) {
            val sampleId = pool.load(context, resId, 1)
            sampleToResId[sampleId] = resId
        }

        scope.launch {
            runCatching { deferred.await() }
                .onSuccess { sampleId ->
                    pool.play(sampleId, 1f, 1f, 1, 0, 1f)
                }
                .onFailure {
                    loadedSounds.remove(resId)
                }
        }
    }

    fun pauseAll() {
        soundPool?.autoPause()
    }

    fun resumeAll() {
        soundPool?.autoResume()
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        loadedSounds.clear()
        loadingSounds.clear()
        sampleToResId.clear()
        appContext = null
    }
}
