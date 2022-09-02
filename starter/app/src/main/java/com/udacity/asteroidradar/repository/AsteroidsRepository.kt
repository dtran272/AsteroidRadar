package com.udacity.asteroidradar.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repository for managing asteroid data incoming from the network to internal disk.
 *
 * @property database Database of Asteroid data
 */
class AsteroidsRepository(private val database: AsteroidDatabase) {

    /**
     * A feed of asteroids data
     */
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getFeed()) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteOldData() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteByDate(getFormattedCurrentDate())
        }
    }

    /**
     * Refreshes the asteroid data stored in cache
     */
    suspend fun refreshFeed() {
        fetchAsteroidFeed()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshTodayFeed() {
        fetchAsteroidFeed(getFormattedCurrentDate())
    }

    private suspend fun fetchAsteroidFeed(endDateQuery: String? = null) {
        withContext(Dispatchers.IO) {
            try {
                Timber.i("Fetch asteroid feed from network")
                val feedResult = NasaApi.neoRetrofitService.getAsteroidsFeed(endDate = endDateQuery)
                val asteroidsFeed = parseAsteroidsJsonResult(JSONObject(feedResult))

                Timber.i("Save feed to local cache")
                database.asteroidDao.insertAll(*asteroidsFeed.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFormattedCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}