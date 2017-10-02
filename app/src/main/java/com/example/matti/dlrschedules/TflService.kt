package com.example.matti.dlrschedules

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface TflService {
    @GET("StopPoint/{station_id}/arrivals")
    fun getArrivals(@Path("station_id") stationId: String): Call<List<Arrival>>
}

data class Arrival(
        val lineName: String,
        val destinationName: String,
        val timeToStation: Int,
        val platformName: String
)

object TflApi {
    private val restAdapter by lazy {
        Retrofit.Builder()
                .baseUrl("https://api.tfl.gov.uk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    val service by lazy {
        restAdapter.create(TflService::class.java)
    }
}
