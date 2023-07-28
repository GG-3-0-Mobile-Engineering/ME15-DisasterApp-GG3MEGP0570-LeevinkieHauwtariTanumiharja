package com.gigih.disastermap.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NavUtils
import com.gigih.disastermap.data.SharedPreferencesManager
import com.gigih.disastermap.databinding.ActivitySettingsBinding

class settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change title app bar
        supportActionBar?.title = "Settings"

        val savedThemeMode = SharedPreferencesManager.getThemeMode(this)
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)

        // Set state Switch based on saved theme mode.
        binding.themeSwitch.isChecked = savedThemeMode != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val themeMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(themeMode)
            // Save theme mode
            SharedPreferencesManager.setThemeMode(this, themeMode)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Navigate back to the parent activity
        NavUtils.navigateUpFromSameTask(this)
        return true
    }
}
