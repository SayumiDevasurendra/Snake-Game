package com.example.snakegame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // variables for animation
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation
    private lateinit var image: ImageView
    private lateinit var topic: TextView
    private lateinit var playbtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        //Hooks
        topic = findViewById(R.id.welcome)
        playbtn = findViewById(R.id.playButton)
        image = findViewById(R.id.snakeLogo)

        image.startAnimation(topAnim)
        topic.startAnimation(bottomAnim)
        playbtn.startAnimation(bottomAnim)

        playbtn.setOnClickListener {
            startActivity(Intent(this, Game::class.java))
        }
    }
}