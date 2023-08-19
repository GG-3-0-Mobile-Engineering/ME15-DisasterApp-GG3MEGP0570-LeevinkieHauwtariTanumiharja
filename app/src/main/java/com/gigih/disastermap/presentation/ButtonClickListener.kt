package com.gigih.disastermap.presentation

import android.content.Context
import android.view.View
import com.gigih.disastermap.R
import com.gigih.disastermap.databinding.ActivityMapsBinding
import com.gigih.disastermap.utils.LoadingManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ButtonClickListener(
    private val mapsActivity: Maps,
    private val loadingManager: LoadingManager,
    private val binding: ActivityMapsBinding
) : View.OnClickListener {

    override fun onClick(view: View) {
        when (view.id) {
            binding.efabFlood.id,
            binding.efabEarthquake.id,
            binding.efabWind.id,
            binding.efabHaze.id,
            binding.efabFire.id,
            binding.efabVolcano.id,
            binding.efabDefault.id -> {
                val efab = view as ExtendedFloatingActionButton
                val colorResId = R.color.green // Change to your desired color

                loadingManager.showLoadingAnimation()

                GlobalScope.launch {
                    delay(2000)
                    val selectedDisasterType = when (efab.id) {
                        binding.efabFlood.id -> "flood"
                        binding.efabEarthquake.id -> "earthquake"
                        binding.efabWind.id -> "wind"
                        binding.efabHaze.id -> "haze"
                        binding.efabFire.id -> "fire"
                        binding.efabVolcano.id -> "volcano"
                        binding.efabDefault.id -> ""
                        else -> ""
                    }

                    withContext(Dispatchers.Main) {
                        mapsActivity.listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                        mapsActivity.resetViews()
                        mapsActivity.setEfFabStyle(efab, colorResId)
                        mapsActivity.lastClickedEfFab = efab
                        loadingManager.hideLoadingAnimation()
                    }
                }
            }
        }
    }
}