package com.gigih.disastermap.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gigih.disastermap.R
import com.gigih.disastermap.databinding.ActivityMapsBinding
import com.gigih.disastermap.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.top_text_anim)
        val middleAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.bottom_text_anim)
        val bottomAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.middle_text_anim)

        binding.topTV.startAnimation(topAnimation)
        binding.middleTV.startAnimation(middleAnimation)
        binding.bottomTV.startAnimation(bottomAnimation)

        supportActionBar?.hide()

        val splashScreenTimeOut: Long = 3000

        Handler(Looper.myLooper() ?: return).postDelayed({
                goToMapsActivity()
        }, splashScreenTimeOut)

    }

    private fun goToMapsActivity() {
        val mapsActivityIntent = Intent(this@SplashScreen, MapsActivity::class.java)
        startActivity(mapsActivityIntent)
        finish()
    }
}