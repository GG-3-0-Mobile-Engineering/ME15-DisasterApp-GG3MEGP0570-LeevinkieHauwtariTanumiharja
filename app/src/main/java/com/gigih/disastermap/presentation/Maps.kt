package com.gigih.disastermap.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gigih.disastermap.R
import com.gigih.disastermap.adapter.ListDisasterAdapter
import com.gigih.disastermap.api.GeometriesItem
import com.gigih.disastermap.data.SharedPreferencesManager
import com.gigih.disastermap.databinding.ActivityMapsBinding
import com.gigih.disastermap.domain.DisasterDomain
import com.gigih.disastermap.utils.LoadingManager
import com.gigih.disastermap.utils.MessageHelper
import com.gigih.disastermap.utils.NotificationManager
import com.gigih.disastermap.utils.PermissionManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Maps : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityMapsBinding
    lateinit var listDisasterAdapter : ListDisasterAdapter
    private lateinit var notificationManager: NotificationManager
    private lateinit var permissionManager: PermissionManager
    private lateinit var loadingManager: LoadingManager
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mLocationClient: FusedLocationProviderClient
    var collapse=true
    private val viewModel: MapsViewModel by viewModels()
    private var selectedDisasterType: String = ""
    var lastClickedEfFab: ExtendedFloatingActionButton? = null
    private val searchMarkers: MutableList<Pair<LatLng, String>> = mutableListOf()
    private val originalButtonStyles: MutableMap<ExtendedFloatingActionButton, Pair<ColorStateList, ColorStateList>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        notificationManager = NotificationManager(this)
        permissionManager = PermissionManager(this, binding)
        loadingManager = LoadingManager(binding)

        if (permissionManager.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Permission is already granted, show a snackbar
            initMap()
            MessageHelper.showSnackbar(binding.root, "Location permission is already granted.")
        } else {
            // Permission is not granted, request permissions
            permissionManager.requestPermissions()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        val fabClickListener = ButtonClickListener(this, loadingManager, binding)

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

        originalButtonStyles[binding.efabFlood] =
            Pair(binding.efabFlood.backgroundTintList, binding.efabFlood.textColors) as Pair<ColorStateList, ColorStateList>
        originalButtonStyles[binding.efabEarthquake] =
            Pair(binding.efabEarthquake.backgroundTintList, binding.efabEarthquake.textColors) as Pair<ColorStateList, ColorStateList>
        originalButtonStyles[binding.efabWind] =
            Pair(binding.efabWind.backgroundTintList, binding.efabWind.textColors) as Pair<ColorStateList, ColorStateList>
        originalButtonStyles[binding.efabHaze] =
            Pair(binding.efabHaze.backgroundTintList, binding.efabHaze.textColors) as Pair<ColorStateList, ColorStateList>
        originalButtonStyles[binding.efabFire] =
            Pair(binding.efabFire.backgroundTintList, binding.efabFire.textColors) as Pair<ColorStateList, ColorStateList>
        originalButtonStyles[binding.efabVolcano] =
            Pair(binding.efabVolcano.backgroundTintList, binding.efabVolcano.textColors) as Pair<ColorStateList, ColorStateList>
        originalButtonStyles[binding.efabDefault] =
            Pair(binding.efabDefault.backgroundTintList, binding.efabDefault.textColors) as Pair<ColorStateList, ColorStateList>


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
            val intent = Intent(this, com.gigih.disastermap.presentation.Settings::class.java)
            themeSelectionResult.launch(intent)
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

        binding.efabFlood.setOnClickListener(fabClickListener)
        binding.efabEarthquake.setOnClickListener(fabClickListener)
        binding.efabWind.setOnClickListener(fabClickListener)
        binding.efabHaze.setOnClickListener(fabClickListener)
        binding.efabFire.setOnClickListener(fabClickListener)
        binding.efabVolcano.setOnClickListener(fabClickListener)
        binding.efabDefault.setOnClickListener(fabClickListener)

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
                    MessageHelper.showSnackbar(binding.root, "Enter a province name to search.")
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
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)


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
                            notificationManager.showFloodNotification(floodDepth)
                            break // Only show the notification once for the first matching report
                        }
                    }
                }
            }
        } else {
            // Clear the map and show snackbar when search query is empty
            mGoogleMap?.clear()
            MessageHelper.showSnackbar(binding.root, "Enter a province name to search.")
        }
    }


    private val themeSelectionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Theme selection result received, finish the activity to go back
                finish()
            }
        }
    fun resetViews() {
        lastClickedEfFab?.let {
            val originalStyle = originalButtonStyles[it]
            it.backgroundTintList = originalStyle?.first
            if (originalStyle != null) {
                it.setTextColor(originalStyle.second)
            }
        }
    }

    fun setEfFabStyle(efab: ExtendedFloatingActionButton, colorResId: Int) {
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
}