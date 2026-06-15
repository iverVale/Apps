package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val platform: String,
    val genre: String,
    val rating: Float = 0.0f, // 0.0f means not rated yet
    val review: String? = null,
    val isWishlist: Boolean = false,
    val reviewDate: Long = 0L,
    val releaseYear: String = "",
    val developer: String = "",
    val description: String = ""
)
