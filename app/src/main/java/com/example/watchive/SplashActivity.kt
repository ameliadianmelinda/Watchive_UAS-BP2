package com.example.watchive

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logoWatchive)
        val text = findViewById<TextView>(R.id.textWatchive)

        // Fade-in
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 2000
        }

        // Slide-up
        val slideUp = TranslateAnimation(0f, 0f, 80f, 0f).apply {
            duration = 2000
        }

        logo.startAnimation(fadeIn)
        text.startAnimation(slideUp)

        // Delay → move to Login
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)
    }
}
