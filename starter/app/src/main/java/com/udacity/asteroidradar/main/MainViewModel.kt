package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val asteroidsRepository = AsteroidsRepository(AsteroidDatabase.getInstance(application))

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    val asteroids = asteroidsRepository.asteroids

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
                Timber.e(e)

                _pictureOfDay.value = PictureOfDay(
                    mediaType = "",
                    title = "No Image",
                    url = ""
                )
            }
        }
    }

    private fun getAsteroidsFeed() {
        viewModelScope.launch {
            asteroidsRepository.refreshFeed()
        }
    }
}