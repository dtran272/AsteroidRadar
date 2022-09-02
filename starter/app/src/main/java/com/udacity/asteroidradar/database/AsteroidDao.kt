package com.udacity.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Defines methods for using the Asteroid class with Room
 *
 */
@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidEntity)

    @Query("select * from AsteroidEntity order by closeApproachDate asc")
    fun getAllFeed(): List<AsteroidEntity>

    @Query("select * from AsteroidEntity where closeApproachDate =:date order by closeApproachDate asc")
    fun getFeedByDate(date: String): List<AsteroidEntity>

    @Query("select * from AsteroidEntity where closeApproachDate >=:startDate and closeApproachDate <=:endDate order by closeApproachDate asc")
    fun getFeedByDateRange(startDate: String, endDate: String): List<AsteroidEntity>

    @Query("delete from AsteroidEntity where closeApproachDate <:date")
    fun deleteByDate(date: String)
}