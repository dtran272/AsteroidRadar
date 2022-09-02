package com.udacity.asteroidradar.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class RefreshFeedWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshFeedWorker"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val asteroidRepository = AsteroidsRepository(AsteroidDatabase.getInstance(applicationContext))

        return try {
            asteroidRepository.deleteOldData()
            asteroidRepository.refreshTodayFeed()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}