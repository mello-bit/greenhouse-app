package com.example.greenhouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.greenhouse_app.databinding.ActivityMainBinding
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.fragments.ChartsFragment
import com.example.greenhouse_app.fragments.HomeFragment
import com.example.greenhouse_app.fragments.NotificationFragment
import com.example.greenhouse_app.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val homeFragment = HomeFragment()
        val settingsFragment = SettingsFragment()
        val notificationFragment = NotificationFragment()
        val chartsFragment = ChartsFragment()
        setCurrentFragment(settingsFragment)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    setCurrentFragment(settingsFragment)
                    true
                }
                R.id.nav_home -> {
                    setCurrentFragment(homeFragment)
                    true
                }
                R.id.nav_notification -> {
                    setCurrentFragment(notificationFragment)
                    true
                }
                R.id.nav_chart -> {
                    setCurrentFragment(chartsFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}