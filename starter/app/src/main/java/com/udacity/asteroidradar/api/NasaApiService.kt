package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor { apiKeyAsQuery(it) }
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitNeo = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL + "neo/rest/v1/")
    .client(okHttpClient)   // Set custom http client
    .build()

private val retrofitApod = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL + "planetary/")
    .client(okHttpClient)   // Set custom http client
    .build()

interface ApodApiService {

    @GET("apod")
    suspend fun getPictureOfTheDay(): PictureOfDay
}

interface NeoApiService {

    @GET("feed")
    suspend fun getAsteroidsFeed(@Query("start_date") startDate: String? = null, @Query("end_date") endDate: String? = null): String
}

object NasaApi {
    val neoRetrofitService: NeoApiService by lazy {
        retrofitNeo.create(NeoApiService::class.java)
    }

    val apodRetrofitService: ApodApiService by lazy {
        retrofitApod.create(ApodApiService::class.java)
    }
}