package com.example.project5

import android.graphics.RectF
import android.graphics.Color
import kotlin.random.Random

class BrickBreaker {
    // Data class representing a brick.
    data class Brick(val rect: RectF, val color: Int, var hit: Boolean = false)

    // List of bricks in the game.
    var bricks: MutableList<Brick> = mutableListOf()

    // Ball properties.
    var ballX: Float = 0f
    var ballY: Float = 0f
    var ballRadius: Float = 20f
    private var ballSpeedX: Float = 8f
    private var ballSpeedY: Float = 8f

    // Paddle represented as a rectangle.
    var paddleRect: RectF = RectF()

    // Game state.
    var gameOver: Boolean = false
    var score: Int = 0
    var newBest: Boolean = false

    // Flag used to trigger a sound when the ball hits the paddle.
    var paddleHit: Boolean = false

    // Initializes bricks based on the current screen width.
    private fun initializeBricks(screenWidth: Int) {
        bricks.clear()
        val rows = 4
        val cols = 6
        val brickWidth = screenWidth / cols.toFloat()
        val brickHeight = 50f
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val left = col * brickWidth
                val top = row * brickHeight + 50  // margin from the top.
                val right = left + brickWidth - 5   // slight gap between bricks.
                val bottom = top + brickHeight - 5
                // Each brick is given a random color.
                val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
                bricks.add(Brick(RectF(left, top, right, bottom), color))
            }
        }
    }

    // Initializes the ball at the center of the screen and the paddle at the bottom.
    private fun initializeBallAndPaddle(screenWidth: Int, screenHeight: Int) {
        // Place ball at center of screen.
        ballX = screenWidth / 2f
        ballY = screenHeight / 2f

        // Place paddle at the bottom center.
        val paddleWidth = screenWidth / 4f
        val paddleHeight = 30f
        val left = (screenWidth - paddleWidth) / 2f
        val top = screenHeight - paddleHeight - 50
        paddleRect = RectF(left, top, left + paddleWidth, top + paddleHeight)
    }

    // Updates the game state: moves the ball, checks for collisions, and updates the score.
    fun update(screenWidth: Int, screenHeight: Int) {
        // Do initial setup if necessary.
        if (bricks.isEmpty()) {
            initializeBricks(screenWidth)
            initializeBallAndPaddle(screenWidth, screenHeight)
        }

        // Move the ball.
        ballX += ballSpeedX
        ballY += ballSpeedY

        // Bounce off the left and right walls.
        if (ballX - ballRadius < 0 || ballX + ballRadius > screenWidth) {
            ballSpeedX = -ballSpeedX
        }
        // Bounce off the top wall.
        if (ballY - ballRadius < 0) {
            ballSpeedY = -ballSpeedY
        }
        // Bounce off the paddle.
        if (ballY + ballRadius >= paddleRect.top && ballX in paddleRect.left..paddleRect.right) {
            ballSpeedY = -ballSpeedY
            paddleHit = true
        }
        // Check collision with bricks.
        for (brick in bricks) {
            if (!brick.hit && brick.rect.contains(ballX, ballY)) {
                brick.hit = true
                ballSpeedY = -ballSpeedY
                score++
                break  // Only one brick collision per update.
            }
        }
        // Check if the ball goes below the screen (missed the paddle).
        if (ballY - ballRadius > screenHeight) {
            gameOver = true
        }
    }

    // Moves the paddle so that its center is at the given x-coordinate.
    fun movePaddle(x: Float, screenWidth: Int) {
        val paddleWidth = paddleRect.width()
        var newLeft = x - paddleWidth / 2
        if (newLeft < 0) newLeft = 0f
        if (newLeft + paddleWidth > screenWidth) newLeft = screenWidth - paddleWidth
        paddleRect.offsetTo(newLeft, paddleRect.top)
    }

    // Returns the number of bricks that have not been hit.
    fun bricksLeft(): Int {
        return bricks.count { !it.hit }
    }
}
