package com.example.watchive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomnav)
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    openFragment(HomeFragment())
                    true
                }
                R.id.navigation_watchlist -> {
                    openFragment(WatchlistFragment())
                    true
                }
                R.id.navigation_profile -> {
                    openFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
