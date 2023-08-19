package com.gigih.disastermap.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.gigih.disastermap.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change title app bar
        supportActionBar?.title = "Settings"

        val isNightModeEnabled = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.themeSwitch.isChecked = isNightModeEnabled

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setThemeMode(isChecked)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Navigate back to the parent activity
        NavUtils.navigateUpFromSameTask(this)
        return true
    }
}
