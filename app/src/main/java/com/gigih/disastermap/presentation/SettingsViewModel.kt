package com.gigih.disastermap.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.appcompat.app.AppCompatDelegate
import com.gigih.disastermap.data.SharedPreferencesManager
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _themeSwitchState = MutableLiveData<Boolean>()
    val themeSwitchState: LiveData<Boolean>
        get() = _themeSwitchState

    init {
        _themeSwitchState.value = AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun setThemeMode(isChecked: Boolean) {
        val themeMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(themeMode)
        SharedPreferencesManager.setThemeMode(getApplication(), themeMode)
        _themeSwitchState.value = isChecked
    }
}
