package com.gigih.disastermap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gigih.disastermap.R
import com.gigih.disastermap.api.ApiResponse
import com.gigih.disastermap.api.GeometriesItem
import com.gigih.disastermap.api.Properties
import com.gigih.disastermap.data.ExistingProvince
import com.gigih.disastermap.databinding.ItemListDisasterBinding
import com.gigih.disastermap.domain.DisasterDomain
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListDisasterAdapter( val apiResponse: ApiResponse): RecyclerView.Adapter<ListDisasterAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemListDisasterBinding): RecyclerView.ViewHolder(binding.root)


    private val defaultDisasterTypes = listOf("flood", "earthquake", "haze", "fire", "wind", "volcano")
    var filteredGeometries: List<GeometriesItem> =
        (apiResponse.result?.objects?.output?.geometries ?: emptyList()) as List<GeometriesItem>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListDisasterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        // Check if the list of Properties is not null before returning its size
        return return filteredGeometries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val geometriesItem = filteredGeometries[position]
        val properties = geometriesItem.properties

        properties?.let {
            holder.binding.tvDisasterName.text = it.disasterType
            val provinceName = DisasterDomain.getProvinceNameFromCode(it.tags?.instanceRegionCode)
            holder.binding.tvDisasterLocation.text = provinceName
            val createdAt = DisasterDomain.parseDate(it.createdAt)
            holder.binding.tvDisasterDate.text = createdAt
            Glide.with(holder.itemView.context)
                .load(it.imageUrl)
                .error(R.drawable.danger)
                .into(holder.binding.imgDisaster)

            val lat = geometriesItem.coordinates?.getOrNull(1) ?: 0.0
            val lng = geometriesItem.coordinates?.getOrNull(0) ?: 0.0
            holder.binding.tvLatitude.text = "Latitude: $lat"
            holder.binding.tvLongitude.text = "Longitude: $lng"
        }
    }

    fun filterByDisasterType(disasterType: String) {
        filteredGeometries = if (disasterType in defaultDisasterTypes) {
            apiResponse.result?.objects?.output?.geometries?.filter {
                it?.properties?.disasterType == disasterType
            }?.toList() as List<GeometriesItem>? ?: emptyList()
        } else {
            apiResponse.result?.objects?.output?.geometries as List<GeometriesItem>? ?: emptyList()
        }
        notifyDataSetChanged()
    }

    fun filterByProvinceName(filteredGeometries: List<GeometriesItem>) {
        this.filteredGeometries = filteredGeometries
        notifyDataSetChanged()
    }

}