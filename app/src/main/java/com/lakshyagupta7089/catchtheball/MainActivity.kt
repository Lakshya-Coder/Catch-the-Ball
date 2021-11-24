package com.lakshyagupta7089.catchtheball

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View.GONE
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.lakshyagupta7089.catchtheball.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.floor
import kotlin.math.round


class MainActivity : AppCompatActivity() {
    companion object {
        const val INITIAL_POSITION = -80f
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    // Position
    private var boxY = 0f
    private var orangeX = 0
    private var orangeY = 0
    private var pinkX = 0

    private var pinkY = 0
    private var blackX = 0

    private var blackY = 0

    // Speed
    private var boxSpeed = 10
    private var orangeSpeed = 10
    private var pinkSpeed = 10
    private var blackSpeed = 10

    // Timer
    private var timer: Timer? = Timer()
    private val handler = Handler(Looper.myLooper()!!)

    // Status
    private var actionFlg = false
    private var startFlg = false
    private var pauseFlg = false

    // Size
    private var screenHeight = 0
    private var screenWidth = 0
    private var frameHeight = 0
    private var boxSize = 0

    // Score
    private var score = 0

    // SoundPlayer
    private lateinit var soundPlayer: SoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hiding the actionbar
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundPlayer = SoundPlayer(this)

        // Screen Size
        screenHeight = getHeight(applicationContext)
        screenWidth = getWidth(applicationContext)

        boxSpeed = round(screenHeight / 60f).toInt()
        orangeSpeed = round(screenWidth / 60f).toInt()
        pinkSpeed = round(screenWidth / 36f).toInt()
        blackSpeed = round(screenWidth / 45f).toInt()

        // Initial position
        binding.orange.x = INITIAL_POSITION
        binding.orange.y = INITIAL_POSITION

        binding.black.x = INITIAL_POSITION
        binding.black.y = INITIAL_POSITION

        binding.pink.x = INITIAL_POSITION
        binding.pink.y = INITIAL_POSITION

        binding.scoreLabel.text = getString(R.string.score, score)
    }

    private fun getWidth(mContext: Context): Int {
        val width: Int
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        width = if (Build.VERSION.SDK_INT > 12) {
            val size = Point()
            display.getSize(size)
            size.x
        } else {
            display.getWidth() // Deprecated
        }
        return width
    }

    private fun getHeight(mContext: Context): Int {
        var height = 0
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        height = if (Build.VERSION.SDK_INT > 12) {
            val size = Point()
            display.getSize(size)
            size.y
        } else {
            display.getHeight() // Deprecated
        }
        return height
    }

    private fun changePosition() {
        if (pauseFlg) return

        hitCheck()

        // Orange
        orangeX -= orangeSpeed
        if (orangeX < 0) {
            orangeX = screenWidth + 20
            orangeY = floor(Math.random() * (frameHeight - binding.orange.height)).toInt()
        }
        binding.orange.x = orangeX.toFloat()
        binding.orange.y = orangeY.toFloat()

        // Black
        blackX -= blackSpeed
        if (blackX < 0) {
            blackX = screenWidth + 10
            blackY = floor(Math.random() * (frameHeight - binding.black.height)).toInt()
        }
        binding.black.x = blackX.toFloat()
        binding.black.y = blackY.toFloat()

        // Pink
        pinkX -= pinkSpeed
        if (pinkX < 0) {
            pinkX = screenWidth + 5000
            pinkY = floor(Math.random() * (frameHeight - binding.black.height)).toInt()
        }
        binding.pink.x = pinkX.toFloat()
        binding.pink.y = pinkY.toFloat()

        // Box
        if (actionFlg) {
            // Touching
            boxSpeed = -22
            boxY += boxSpeed
        } else {
            // Releasing
            boxSpeed += 1
            boxY += 0.5f * boxSpeed
        }

        if (boxY < 0) boxY = 0f
        if (boxY > frameHeight - boxSize) boxY = (frameHeight - boxSize).toFloat()

        binding.box.y = boxY
//        binding.scoreLabel.text = "Score: $score"
        binding.scoreLabel.text = getString(R.string.score, score)
    }

    private fun hitCheck() {
        // Orange
        val orangeCenterX = orangeX + binding.orange.width / 2
        val orangeCenterY = orangeY + binding.orange.height / 2

        if (0 <= orangeCenterX && orangeCenterX <= boxSize &&
            boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize
        ) {
            orangeX -= 100
            score += 10
            soundPlayer.playHitSound()
        }

        // Pink
        val pinkCenterX = pinkX + binding.pink.width / 2
        val pinkCenterY = pinkY + binding.pink.height / 2

        if (0 <= pinkCenterX && pinkCenterX <= boxSize &&
            boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize
        ) {
            pinkX -= 100
            score += 30
            soundPlayer.playHitSound()
        }

        // Black
        val blackCenterX = blackX + binding.black.width / 2
        val blackCenterY = blackY + binding.black.height / 2
        if (0 <= blackCenterX && blackCenterX <= boxSize &&
            boxY <= blackCenterY && blackCenterY <= boxY + boxSize
        ) {
            soundPlayer.playOverSound()

            // Game over
            if (timer != null) {
                timer?.cancel()
                timer = null
            }

            // Show ResultActivity
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("SCORE", score)

            startActivity(intent)
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!startFlg) {
            startFlg = true

            // FrameHeight
            frameHeight = binding.frame.height

            // Box
            boxY = binding.box.y
            boxSize = binding.box.height

            binding.startLabel.visibility = GONE

            timer?.schedule(object : TimerTask() {
                override fun run() {
                    handler.post {
                        changePosition()
                    }
                }
            }, 0, 20)
        } else {
            if (event != null) {
                if (event.action == ACTION_DOWN) {
                    actionFlg = true
                } else if (event.action == ACTION_UP) {
                    actionFlg = false
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onPause() {
        mediaPlayer!!.pause()
        pauseFlg = true

        super.onPause()
    }

    override fun onPostResume() {
        mediaPlayer!!.start()
        pauseFlg = false

        super.onPostResume()
    }

    override fun onBackPressed() {}
}