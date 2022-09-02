package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid
import java.util.*


class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getAsteroidsFeed()
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsDone() {
        _navigateToSelectedAsteroid.value = null
    }

    private fun getAsteroidsFeed(startDate: Date? = null, endDate: Date? = null) {
        // TODO retrieve data from cache
        _asteroids.value = listOf(
            Asteroid(
                1,
                "Astro 1",
                "2022-09-01",
                21.9,
                0.2477650126,
                18.3963939763,
                0.4309946888,
                false
            ),
            Asteroid(
                2,
                "Astro 2",
                "2022-09-02",
                40.2,
                0.1238574,
                65.261684,
                0.25154,
                true
            )
        )
    }
}