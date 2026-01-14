package com.example.watchive

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan layout dipasang dulu
        setContentView(R.layout.activity_splash)

        // Gunakan Safe Call untuk menghindari NPE jika ID tidak ditemukan
        val logo = findViewById<ImageView>(R.id.logoWatchive)
        val text = findViewById<TextView>(R.id.textWatchive)

        // Delay lebih singkat (2 detik saja) agar cepat masuk ke login
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                // Jika login crash, kita tahu masalahnya di LoginActivity
                e.printStackTrace()
            }
        }, 2000)
    }
}
