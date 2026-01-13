package com.example.watchive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Ambil NavHostFragment dari layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        // 2. Ambil NavController
        val navController = navHostFragment.navController
        
        // 3. Hubungkan BottomNavigationView dengan NavController
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomnav)
        bottomNavigation.setupWithNavController(navController)
    }
}
