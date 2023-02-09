package com.example.greenhouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val settingsFragment = SettingsFragment()
        val notificationFragment = NotificationFragment()
        val chartsFragment = ChartsFragment()
        replaceFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(homeFragment)
                R.id.nav_settings -> replaceFragment(settingsFragment)
                R.id.nav_notification -> replaceFragment(notificationFragment)
                R.id.nav_chart -> replaceFragment(chartsFragment)
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragment, fragment)
        fragmentTransaction.commit()
    }

}