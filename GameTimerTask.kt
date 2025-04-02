package com.example.project5

import java.util.TimerTask

class GameTimerTask(private val gameView: GameView) : TimerTask() {
    override fun run() {
        // Post the update call to the UI thread.
        gameView.post { gameView.update() }
    }
}
