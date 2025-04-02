package com.example.project5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create our custom game view and set it as the content view.
        val gameView = GameView(this)
        setContentView(gameView)
    }
}
