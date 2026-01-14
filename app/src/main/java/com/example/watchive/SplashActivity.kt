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

        // Animasi Fade-in (Logo & Teks muncul perlahan)
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1500
        }

        // Animasi Slide-up (Teks bergeser sedikit ke atas)
        val slideUp = TranslateAnimation(0f, 0f, 50f, 0f).apply {
            duration = 1500
        }

        logo?.startAnimation(fadeIn)
        text?.startAnimation(slideUp)

        // Tunggu 3 detik lalu pindah ke Login
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
