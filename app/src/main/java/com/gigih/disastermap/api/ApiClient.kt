package com.gigih.disastermap.api

import retrofit2.Retrofit
import javax.inject.Inject

class ApiClient @Inject constructor(private val retrofit: Retrofit) {

    fun getService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}