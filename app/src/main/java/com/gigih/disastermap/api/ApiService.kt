package com.gigih.disastermap.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
        @GET("reports")
        fun getListDisaster(): Call<ApiResponse>

        @GET("reports")
        fun getListDisasterByType(@Query("disaster_type") disasterType: String): Call<ApiResponse>

        @GET("reports")
        fun getListDisasterByPeriod(@Query("timeperiod") timePeriod: Long): Call<ApiResponse>

}