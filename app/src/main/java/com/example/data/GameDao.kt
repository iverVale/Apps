package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY title ASC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE rating > 0.0 OR (review IS NOT NULL AND review != '') ORDER BY reviewDate DESC")
    fun getReviewedGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE isWishlist = 1 ORDER BY id DESC")
    fun getWishlistGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: Int): Flow<Game?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game): Long

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGamesCount(): Int
}
