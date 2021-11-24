package com.lakshyagupta7089.catchtheball

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lakshyagupta7089.catchtheball.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(
                this,
                R.raw.background_music
            )
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }

        // Hiding the actionbar
        supportActionBar?.hide()

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener { startGame() }
    }

    private fun startGame() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {}

    override fun onPause() {
        mediaPlayer!!.pause()
        super.onPause()
    }

    override fun onPostResume() {
        mediaPlayer!!.start()
        super.onPostResume()
    }
}