package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    val allGames: Flow<List<Game>> = gameDao.getAllGames()
    val reviewedGames: Flow<List<Game>> = gameDao.getReviewedGames()
    val wishlistGames: Flow<List<Game>> = gameDao.getWishlistGames()

    fun getGameById(id: Int): Flow<Game?> = gameDao.getGameById(id)

    suspend fun insertGame(game: Game): Long = withContext(Dispatchers.IO) {
        gameDao.insertGame(game)
    }

    suspend fun updateGame(game: Game) = withContext(Dispatchers.IO) {
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: Game) = withContext(Dispatchers.IO) {
        gameDao.deleteGame(game)
    }

    suspend fun prepopulateIfNeeded() = withContext(Dispatchers.IO) {
        if (gameDao.getGamesCount() == 0) {
            val initialGames = listOf(
                Game(
                    title = "Elden Ring",
                    platform = "PC, PS5, Xbox",
                    genre = "RPG",
                    releaseYear = "2022",
                    developer = "FromSoftware",
                    description = "Um épico imersivo de ação e RPG em um mundo aberto sombrio, criado por Hidetaka Miyazaki e George R.R. Martin. Desafiador e misterioso."
                ),
                Game(
                    title = "The Witcher 3: Wild Hunt",
                    platform = "PC, PS5, Xbox, Switch",
                    genre = "RPG",
                    releaseYear = "2015",
                    developer = "CD Projekt Red",
                    description = "Como o lendário caçador de monstros Geralt de Rívia, explore um império em guerra e procure a criança da profecia em um mundo fantástico vivo."
                ),
                Game(
                    title = "The Legend of Zelda: Breath of the Wild",
                    platform = "Switch",
                    genre = "Aventura",
                    releaseYear = "2017",
                    developer = "Nintendo",
                    description = "Esqueça tudo o que você sabe sobre os jogos da série Zelda. Entre em um mundo de descobertas, exploração e aventura neste título revolucionário."
                ),
                Game(
                    title = "Grand Theft Auto V",
                    platform = "PC, PS5, Xbox",
                    genre = "Ação",
                    releaseYear = "2013",
                    developer = "Rockstar Games",
                    description = "Três criminosos muito diferentes alinham seus esforços em uma série de assaltos audaciosos na metrópole ensolarada de Los Santos."
                ),
                Game(
                    title = "Stardew Valley",
                    platform = "PC, Switch, Mobile, Xbox, PS4",
                    genre = "Simulação",
                    releaseYear = "2016",
                    developer = "ConcernedApe",
                    description = "Você herdou a antiga fazenda do seu avô em Stardew Valley. Pegue as ferramentas e comece sua nova vida no campo nesta obra de arte acolhedora."
                ),
                Game(
                    title = "Hades",
                    platform = "PC, PS5, Xbox, Switch",
                    genre = "Roguelike",
                    releaseYear = "2020",
                    developer = "Supergiant Games",
                    description = "Combata os mortos para escapar do submundo grego neste aclamado jogo de ação dungeon crawler com narrativa fascinante."
                )
            )

            for (game in initialGames) {
                gameDao.insertGame(game)
            }
        }
    }
}
