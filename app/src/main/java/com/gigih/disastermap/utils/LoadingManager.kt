package com.gigih.disastermap.utils

import android.view.View
import com.gigih.disastermap.databinding.ActivityMapsBinding


class LoadingManager(private val binding: ActivityMapsBinding){

    fun showLoadingAnimation() {
        binding.loadingAnimationView.visibility = View.VISIBLE
        binding.loadingAnimationView.playAnimation()
    }

    fun hideLoadingAnimation() {
        binding.loadingAnimationView.cancelAnimation()
        binding.loadingAnimationView.visibility = View.GONE
    }

}