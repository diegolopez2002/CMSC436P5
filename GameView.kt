package com.example.project5

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.SoundPool
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.Timer

class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val model: BrickBreaker = BrickBreaker()
    private val paint = Paint()

    // Timer and task for the game loop (roughly 60fps)
    private var timer: Timer? = null
    private var gameTimerTask: GameTimerTask? = null

    // Flag to check if game has started
    private var gameStarted = false

    // Sound for paddle hit
    private var soundPool: SoundPool? = null
    private var hitSoundId: Int = 0

    init {
        // Initialize the SoundPool and load hit.wav from res/raw.
        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        // Make sure you add hit.wav to your res/raw folder and name it exactly hit.wav.
        hitSoundId = soundPool!!.load(context, R.raw.hit, 1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Clear the background.
        canvas.drawColor(Color.WHITE)

        // Draw all the bricks that have not been hit.
        for (brick in model.bricks) {
            if (!brick.hit) {
                paint.color = brick.color
                canvas.drawRect(brick.rect, paint)
            }
        }
        // Draw the paddle as a black rectangle.
        paint.color = Color.BLACK
        canvas.drawRect(model.paddleRect, paint)

        // Draw the ball as a red circle.
        paint.color = Color.RED
        canvas.drawCircle(model.ballX, model.ballY, model.ballRadius, paint)

        // If the game is over, show game over text and scores.
        if (model.gameOver) {
            paint.color = Color.BLUE
            paint.textSize = 50f
            canvas.drawText("Game Over!", width / 4f, height / 2f, paint)
            canvas.drawText("Bricks hit: ${model.score}", width / 4f, height / 2f + 60, paint)
            canvas.drawText("Bricks left: ${model.bricksLeft()}", width / 4f, height / 2f + 120, paint)

            // Retrieve persistent best score from SharedPreferences.
            val prefs = context.getSharedPreferences("BrickBreakerPrefs", Context.MODE_PRIVATE)
            val bestScore = prefs.getInt("bestScore", 0)
            canvas.drawText("Best Score: $bestScore", width / 4f, height / 2f + 180, paint)
            if (model.newBest) {
                canvas.drawText("New Best Score!", width / 4f, height / 2f + 240, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Start the game on first touch.
        if (!gameStarted && event.action == MotionEvent.ACTION_DOWN) {
            gameStarted = true
            startGame()
            // Call performClick() for accessibility.
            performClick()
            return true
        }
        // Move the paddle based on horizontal finger movement.
        if (event.action == MotionEvent.ACTION_MOVE) {
            model.movePaddle(event.x, width)
            invalidate()
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun startGame() {
        timer = Timer()
        gameTimerTask = GameTimerTask(this)
        timer?.schedule(gameTimerTask, 0, 16)  // roughly 60 fps.
    }

    // Called by GameTimerTask on every tick.
    fun update() {
        if (!model.gameOver) {
            model.update(width, height)
            // If the ball hit the paddle in this update, play the sound.
            if (model.paddleHit) {
                soundPool?.play(hitSoundId, 1f, 1f, 1, 0, 1f)
                model.paddleHit = false
            }
            invalidate()
        } else {
            // On game over, update the persistent best score.
            val prefs = context.getSharedPreferences("BrickBreakerPrefs", Context.MODE_PRIVATE)
            val savedBest = prefs.getInt("bestScore", 0)
            if (model.score > savedBest) {
                prefs.edit().putInt("bestScore", model.score).apply()
                model.newBest = true
            }
            timer?.cancel()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        soundPool?.release()
        soundPool = null
    }
}
