package com.gigih.disastermap.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gigih.disastermap.api.ApiResponse
import com.gigih.disastermap.api.ApiService
import com.gigih.disastermap.data.Period
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val _apiResponse = MutableLiveData<ApiResponse>()
    val apiResponse: LiveData<ApiResponse>
        get() = _apiResponse

    init {
        // Fetch the data from the API
        fetchDisasterData()
    }

    fun fetchDisasterData() {
        fetchDisasterDataByPeriod(Period.ONE_WEEK.timeInSeconds)
    }

    // Fetch disaster data for a specific period (e.g., one week, five days, today)
    fun fetchDisasterDataByPeriod(timePeriod: Long) {
        apiService.getListDisasterByPeriod(timePeriod).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _apiResponse.value = response.body()
                } else {
                    _apiResponse.value = null
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _apiResponse.value = null
            }
        })
    }

    // Fetch disaster data for a specific disaster type
    fun fetchDisasterDataByType(disasterType: String) {
        apiService.getListDisasterByType(disasterType).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _apiResponse.value = response.body()
                } else {
                    _apiResponse.value = null
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _apiResponse.value = null
            }
        })
    }
}