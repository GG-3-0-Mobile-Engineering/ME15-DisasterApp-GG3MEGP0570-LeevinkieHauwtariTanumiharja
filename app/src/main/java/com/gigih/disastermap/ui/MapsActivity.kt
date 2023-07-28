package com.gigih.disastermap.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.gigih.disastermap.R
import com.gigih.disastermap.adapter.ListDisasterAdapter
import com.gigih.disastermap.api.GeometriesItem
import com.gigih.disastermap.data.ExistingProvince
import com.gigih.disastermap.data.SharedPreferencesManager
import com.gigih.disastermap.databinding.ActivityMapsBinding
import com.gigih.disastermap.domain.DisasterDomain
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var listDisasterAdapter : ListDisasterAdapter
    private var isPermissionGranted = false
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mLocationClient: FusedLocationProviderClient
    private val GPS_REQUEST_CODE = 9001
    var collapse=true
    private lateinit var viewModel: MapsViewModel
    private var selectedDisasterType: String = ""
    private var lastClickedEfFab: ExtendedFloatingActionButton? = null
    private val searchMarkers: MutableList<Pair<LatLng, String>> = mutableListOf()
    private lateinit var loadingAnimationView: LottieAnimationView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this@MapsActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                isPermissionGranted = true
                initMap()
            } else {
                val intent5 = Intent(Settings.ACTION_APPLICATION_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent5.data = uri
                startActivity(intent5)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        checkMyPermission()
        mLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val layoutManager = LinearLayoutManager(this)
        binding.rvItemDisaster.layoutManager = layoutManager

        selectedDisasterType = ""
        // Fetch data for the default disaster type
        viewModel.fetchDisasterData()

        // Add a divider between items (Optional)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvItemDisaster.context,
            layoutManager.orientation
        )
        binding.rvItemDisaster.addItemDecoration(dividerItemDecoration)

        binding.arrow.setOnClickListener {
            if (collapse){
                bottomSheetBehavior.state= BottomSheetBehavior.STATE_EXPANDED
                collapse=false
            }else{
                bottomSheetBehavior.state= BottomSheetBehavior.STATE_COLLAPSED
                collapse=true
            }
        }

        binding.settings.setOnClickListener {
            val intent = Intent(this, settings::class.java)
            themeSelectionResult.launch(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("water_channel_id", "Water Level Alert", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState== BottomSheetBehavior.STATE_EXPANDED){
                    binding.arrow.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    binding.arrow.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        binding.efabFlood.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = "flood"

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabFlood, R.color.green)
                lastClickedEfFab = binding.efabFlood

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        binding.efabEarthquake.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = "earthquake"

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabEarthquake, R.color.green)
                lastClickedEfFab = binding.efabEarthquake

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        binding.efabWind.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = "wind"

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabWind, R.color.green)
                lastClickedEfFab = binding.efabWind

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        binding.efabHaze.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = "haze"

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabHaze, R.color.green)
                lastClickedEfFab = binding.efabHaze

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        binding.efabFire.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = "fire"

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabFire, R.color.green)
                lastClickedEfFab = binding.efabFire

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        binding.efabVolcano.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = "volcano"

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabVolcano, R.color.green)
                lastClickedEfFab = binding.efabVolcano

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        binding.efabDefault.setOnClickListener {
            showLoadingAnimation()

            GlobalScope.launch {
                delay(2000)
                selectedDisasterType = ""

                // Update the adapter's data based on the selected disaster type on the main thread
                withContext(Dispatchers.Main) {
                    listDisasterAdapter.filterByDisasterType(selectedDisasterType)
                }

                resetViews()
                setEfFabStyle(binding.efabDefault, R.color.green)
                lastClickedEfFab = binding.efabDefault

                // Hide the loading animation on the main thread
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                }
            }
        }

        viewModel.apiResponse.observe(this, { apiResponse ->
            apiResponse?.let {
                // Create the ListDisasterAdapter instance
                listDisasterAdapter = ListDisasterAdapter(it)
                binding.rvItemDisaster.adapter = listDisasterAdapter
            }
        })


        binding.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Call the searchByProvinceName function when the user submits the query
                searchByProvinceName(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()) {
                    // Clear the map and show snackbar when search query is empty
                    mGoogleMap?.clear()
                    showSnackbar("Enter a province name to search.")
                }
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
        // Check for theme change and update it
        val savedThemeMode = SharedPreferencesManager.getThemeMode(this)
        val currentThemeMode = AppCompatDelegate.getDefaultNightMode()
        if (currentThemeMode != savedThemeMode) {
            // Theme has changed, update the theme
            AppCompatDelegate.setDefaultNightMode(savedThemeMode)
            recreate() // Recreate the activity to apply the new theme
        }
    }

    private fun initMap() {
        if (isPermissionGranted) {
            if (isGPSenable()) {
                val supportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                supportMapFragment?.getMapAsync(this)
            }
        }
    }

    private fun searchByProvinceName(provinceName: String) {
        if (provinceName.isNotBlank()) {
            val filteredGeometries = viewModel.apiResponse.value?.result?.objects?.output?.geometries?.filter {
                val properties = it?.properties
                val regionCode = properties?.tags?.instanceRegionCode
                val foundProvinceName = DisasterDomain.getProvinceNameFromCode(regionCode)
                foundProvinceName.equals(provinceName, ignoreCase = true)
            } ?: emptyList()

            listDisasterAdapter.filterByProvinceName(filteredGeometries as List<GeometriesItem>)
            updateMapWithMarkers(filteredGeometries.mapNotNull {
                val lat = it?.coordinates?.getOrNull(1) ?: 0.0
                val lng = it?.coordinates?.getOrNull(0) ?: 0.0
                val markerPosition = LatLng(lat, lng)
                val markerTitle = it?.properties?.disasterType ?: ""
                Pair(markerPosition, markerTitle)
            })

            // Check if the disasterType is "flood" and the provinceName is "DKI Jakarta"
            if (provinceName.equals("DKI Jakarta", ignoreCase = true)) {
                for (geometry in filteredGeometries) {
                    val properties = geometry?.properties
                    val disasterType = properties?.disasterType
                    if (disasterType.equals("flood", ignoreCase = true)) {
                        val reportData = properties?.reportData
                        val floodDepth = reportData?.floodDepth
                        if (floodDepth != null) {
                            // Show the notification
                            showFloodNotification(floodDepth)
                            break // Only show the notification once for the first matching report
                        }
                    }
                }
            }
        } else {
            // Clear the map and show snackbar when search query is empty
            mGoogleMap?.clear()
            showSnackbar("Enter a province name to search.")
        }
    }

    private fun isGPSenable(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (providerEnable) {
            return true
        } else {
            AlertDialog.Builder(this)
                .setTitle("GPS Permission")
                .setMessage("GPS is required for this app to work. Please enable GPS")
                .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, GPS_REQUEST_CODE)
                }
                .setCancelable(false)
                .show()
        }
        return false
    }

    private fun checkMyPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showFloodNotification(floodDepth: Int) {
        val channelId = "water_channel_id"
        val notificationId = 1 // You can use a unique ID for different notifications

        // Create the notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Water Level Alert", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Flood Alert")
            .setContentText("Water level is ${floodDepth} cm in DKI Jakarta.")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }


    private val themeSelectionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Theme selection result received, finish the activity to go back
                finish()
            }
        }

    private fun resetViews() {
        lastClickedEfFab?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@MapsActivity, R.color.white))
            setTextColor(ContextCompat.getColor(this@MapsActivity, R.color.black)) // Change to your desired default text color
        }
    }

    private fun setEfFabStyle(efab: ExtendedFloatingActionButton, colorResId: Int) {
        efab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, colorResId))
        efab.setTextColor(ContextCompat.getColor(this, android.R.color.white)) // Change to your desired clicked text color
    }

    private fun updateMapWithMarkers(markers: List<Pair<LatLng, String>>) {
        mGoogleMap?.clear() // Clear existing markers
        for (marker in markers) {
            mGoogleMap?.addMarker(MarkerOptions().position(marker.first).title(marker.second))
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mGoogleMap?.isMyLocationEnabled = true

        // Display markers from searchMarkers list
        updateMapWithMarkers(searchMarkers)

        // Update the map markers based on the filtered geometries
        val filteredMarkers: MutableList<Pair<LatLng, String>> = mutableListOf()
        for (geometry in viewModel.apiResponse.value?.result?.objects?.output?.geometries ?: emptyList()) {
            val properties = geometry?.properties
            val lat = geometry?.coordinates?.getOrNull(1) ?: 0.0
            val lng = geometry?.coordinates?.getOrNull(0) ?: 0.0
            val markerPosition = LatLng(lat, lng)
            val markerTitle = properties?.disasterType ?: ""
            filteredMarkers.add(Pair(markerPosition, markerTitle))
        }
        updateMapWithMarkers(filteredMarkers)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoadingAnimation() {
        binding.loadingAnimationView.visibility = View.VISIBLE
        binding.loadingAnimationView.playAnimation()
    }


    private fun hideLoadingAnimation() {
        binding.loadingAnimationView.cancelAnimation()
        binding.loadingAnimationView.visibility = View.GONE
    }


}