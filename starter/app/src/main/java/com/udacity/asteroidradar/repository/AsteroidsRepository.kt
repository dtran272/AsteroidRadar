package com.udacity.asteroidradar.repository

import android.os.Build
import androidx.annotation.RequiresApi
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

    suspend fun getAllFeed(): List<Asteroid> {
        return withContext(Dispatchers.IO) {
            val feed = database.asteroidDao.getAllFeed()

            feed.asDomainModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeeklyFeed(): List<Asteroid> {
        return withContext(Dispatchers.IO) {
            val startDate = getFormattedDate()
            val endDate = getFormattedDate(7)
            val feed = database.asteroidDao.getFeedByDateRange(startDate, endDate)

            feed.asDomainModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodayFeed(): List<Asteroid> {
        return withContext(Dispatchers.IO) {
            val feed = database.asteroidDao.getFeedByDate(getFormattedDate())

            feed.asDomainModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteOldData() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteByDate(getFormattedDate())
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
        fetchAsteroidFeed(getFormattedDate())
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
    private fun getFormattedDate(daysFromCurrentDate: Long = 0): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return LocalDate.now().plusDays(daysFromCurrentDate).format(formatter)
    }
}