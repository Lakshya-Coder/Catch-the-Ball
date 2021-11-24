package com.lakshyagupta7089.catchtheball

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.AudioAttributes.USAGE_GAME
import android.media.AudioManager.STREAM_MUSIC
import android.media.SoundPool
import android.os.Build

class SoundPlayer(context: Context ) {
    companion object {
        var soundPool: SoundPool? = null
    }
    private var audioAttributes: AudioAttributes? = null
    private var hitSound = 0
    private var overSound = 0

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // SoundPoll is deprecated in API level 21.(Lollipop)
            audioAttributes = AudioAttributes.Builder()
                .setUsage(USAGE_GAME)
                .setContentType(CONTENT_TYPE_MUSIC)
                .build()
            soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build()
        } else {
            soundPool = SoundPool(2, STREAM_MUSIC,  0)
        }

        hitSound = soundPool?.load(context, R.raw.hit, 1)!!
        overSound = soundPool?.load(context, R.raw.over, 1)!!
    }

    fun playHitSound() {
        soundPool?.play(
            hitSound,
            1.0f,
            1.0f,
            1,
            0,
            1f
        )
    }

    fun playOverSound() {
        soundPool?.play(
            overSound,
            1.0f,
            1.0f,
            1,
            0,
            1f
        )
    }
}