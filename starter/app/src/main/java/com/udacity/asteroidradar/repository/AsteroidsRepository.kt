package com.udacity.asteroidradar.repository

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

    /**
     * Refreshes the asteroid data stored in cache
     */
    suspend fun refreshFeed() {
        withContext(Dispatchers.IO) {
            Timber.i("Fetch asteroid feed from network")
            val feedResult = NasaApi.neoRetrofitService.getAsteroidsFeed()
            val asteroidsFeed = parseAsteroidsJsonResult(JSONObject(feedResult))

            Timber.i("Save feed to local cache")
            database.asteroidDao.insertAll(*asteroidsFeed.asDatabaseModel())
        }
    }
}