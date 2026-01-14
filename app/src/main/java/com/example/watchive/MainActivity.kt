package com.example.watchive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomnav)
        bottomNavigation.setupWithNavController(navController)
        
        // Kita biarkan Bottom Navigation menggunakan warna default dari XML agar tidak berubah-ubah
    }
}
