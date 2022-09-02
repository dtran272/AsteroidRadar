package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.util.*


class MainViewModel : ViewModel() {

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay


    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getPictureOfDay()
        getAsteroidsFeed()
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsDone() {
        _navigateToSelectedAsteroid.value = null
    }


    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                val picOfDay = NasaApi.apodRetrofitService.getPictureOfTheDay()

                _pictureOfDay.value = picOfDay
            } catch (e: Exception) {
                // TODO: Add error handling
                Timber.e(e)
            }
        }
    }

    private fun getAsteroidsFeed(startDate: Date? = null, endDate: Date? = null) {
        viewModelScope.launch {
            try {
                val response = NasaApi.neoRetrofitService.getAsteroidsFeed()
                val asteroidArray = parseAsteroidsJsonResult(JSONObject(response))

                _asteroids.value = asteroidArray

                // TODO: Add all data to cache
            } catch (e: Exception) {
                // TODO: Add error handling
                Timber.e(e)
            }
        }
    }
}