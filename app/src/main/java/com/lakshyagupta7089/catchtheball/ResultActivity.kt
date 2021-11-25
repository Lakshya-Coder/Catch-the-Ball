package com.lakshyagupta7089.catchtheball

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lakshyagupta7089.catchtheball.databinding.ActivityResultBinding
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hiding the actionbar
        supportActionBar?.hide()

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Score
        val score = intent.getIntExtra("SCORE", 0)
        binding.scoreLabel.text = getString(R.string.result_score, score)

        // High Score
        val sharedPreferences = getSharedPreferences(
            "GAME_DATA",
            Context.MODE_PRIVATE
        )
        var highScore = sharedPreferences.getInt("HIGH_SCORE", 0)

        if (score > highScore) {
            // Update the HighScore
            val editor = sharedPreferences.edit()
            editor.putInt("HIGH_SCORE", score)
            editor.apply()

            binding.highScoreLabel.text = getString(R.string.new_result_high_score, score)

            // Show confetti
            binding.viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square, Shape.Circle)
                .addSizes(Size(12))
                .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
                .streamFor(300, 5000L)
        } else {
            binding.highScoreLabel.text = getString(R.string.result_high_score, highScore)
        }

//        CommonConfetti.rainingConfetti(binding.root, intArrayOf(
//            Color.YELLOW,
//            Color.GREEN,
//            Color.MAGENTA,
//        )).oneShot()

        // Setting up onClickListener on tryAgainButton
        binding.tryAgainButton.setOnClickListener { tryAgain() }
    }

    private fun tryAgain() {
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