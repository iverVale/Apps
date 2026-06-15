package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Game
import com.example.data.GameDatabase
import com.example.data.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    val allGames: Flow<List<Game>>
    val reviewedGames: Flow<List<Game>>
    val wishlistGames: Flow<List<Game>>

    // Filters and search
    val selectedPlatform = MutableStateFlow("Todos")
    val selectedGenre = MutableStateFlow("Todos")
    val searchQuery = MutableStateFlow("")

    val filteredGames: StateFlow<List<Game>>

    init {
        val gameDao = GameDatabase.getDatabase(application).gameDao()
        repository = GameRepository(gameDao)

        allGames = repository.allGames
        reviewedGames = repository.reviewedGames
        wishlistGames = repository.wishlistGames

        // Trigger database pre-population
        viewModelScope.launch {
            repository.prepopulateIfNeeded()
        }

        // Live filter matching
        filteredGames = combine(
            repository.allGames,
            selectedPlatform,
            selectedGenre,
            searchQuery
        ) { games, platform, genre, query ->
            games.filter { game ->
                val matchesPlatform = platform == "Todos" || game.platform.contains(platform, ignoreCase = true)
                val matchesGenre = genre == "Todos" || game.genre.equals(genre, ignoreCase = true)
                val matchesSearch = query.isEmpty() || 
                        game.title.contains(query, ignoreCase = true) || 
                        game.developer.contains(query, ignoreCase = true) ||
                        game.description.contains(query, ignoreCase = true)
                
                matchesPlatform && matchesGenre && matchesSearch
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun selectPlatform(platform: String) {
        selectedPlatform.value = platform
    }

    fun selectGenre(genre: String) {
        selectedGenre.value = genre
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    // Add a new game custom
    fun addNewGame(
        title: String,
        platform: String,
        genre: String,
        releaseYear: String,
        developer: String,
        description: String,
        isWishlist: Boolean = false
    ) {
        viewModelScope.launch {
            val game = Game(
                title = title,
                platform = platform,
                genre = genre,
                releaseYear = releaseYear,
                developer = developer,
                description = description,
                isWishlist = isWishlist
            )
            repository.insertGame(game)
        }
    }

    // Submit review & rating
    fun submitReview(game: Game, rating: Float, reviewText: String) {
        viewModelScope.launch {
            val updatedGame = game.copy(
                rating = rating,
                review = reviewText.trim(),
                reviewDate = System.currentTimeMillis()
            )
            repository.updateGame(updatedGame)
        }
    }

    // Delete a review (resets rating and text)
    fun deleteReview(game: Game) {
        viewModelScope.launch {
            val updatedGame = game.copy(
                rating = 0.0f,
                review = null,
                reviewDate = 0L
            )
            repository.updateGame(updatedGame)
        }
    }

    // Toggle wishlist status
    fun toggleWishlist(game: Game) {
        viewModelScope.launch {
            val updatedGame = game.copy(
                isWishlist = !game.isWishlist
            )
            repository.updateGame(updatedGame)
        }
    }

    // Delete a game entirely
    fun deleteGame(game: Game) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }
}

class GameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
