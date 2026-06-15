package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Game
import com.example.ui.GameViewModel
import com.example.ui.theme.StarActive
import com.example.ui.theme.StarInactive
import java.text.SimpleDateFormat
import java.util.*

// Helper to format review timestamps
fun formatReviewDate(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
    return sdf.format(Date(timestamp))
}

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
    starSize: Int = 30
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Estrela $i",
                tint = if (isSelected) StarActive else StarInactive,
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable(enabled = onRatingChanged != null) {
                        onRatingChanged?.invoke(i.toFloat())
                    }
                    .testTag("star_$i")
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LibraryScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onGameClicked: (Game) -> Unit
) {
    val games by viewModel.filteredGames.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val platformOptions = listOf("Todos", "PC", "PS5", "Xbox", "Switch", "Mobile")
    val genreOptions = listOf("Todos", "RPG", "Ação", "Aventura", "Simulação", "Roguelike", "Estratégia", "Outro")

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            prefix = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            suffix = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                    }
                }
            },
            placeholder = { Text("Buscar jogos ou desenvolvedores...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("search_bar"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Featured Game Card (Trending Section matches Sophisticated Dark mockup)
        if (searchQuery.isEmpty() && selectedPlatform == "Todos" && selectedGenre == "Todos") {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(vertical = 4.dp)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable {
                        val featuredGame = games.find { it.title.contains("Elden Ring", ignoreCase = true) } ?: games.firstOrNull()
                        if (featuredGame != null) {
                            onGameClicked(featuredGame)
                        }
                    }
                    .testTag("featured_game_banner")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Gorgeous gradient representing the sophisticated brand highlights
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF381E72).copy(alpha = 0.5f),
                                        Color(0xFF1C1B1F).copy(alpha = 0.8f) // solid feel at base
                                    )
                                )
                            )
                    )
                    
                    // Gamepad silhouette overlay background
                    Icon(
                        imageVector = Icons.Default.Gamepad,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.04f),
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 24.dp, y = 24.dp)
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Destaque",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF381E72)
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "4.9",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Elden Ring: Shadow of the Erdtree",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = "FromSoftware Inc. • RPG de Ação",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCAC4D0),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Platform horizontal chips
        Text(
            text = "Plataformas",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            items(platformOptions) { platform ->
                FilterChip(
                    selected = selectedPlatform == platform,
                    onClick = { viewModel.selectPlatform(platform) },
                    label = { Text(platform) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("chip_platform_$platform")
                )
            }
        }

        // Genre horizontal chips
        Text(
            text = "Gêneros",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            items(genreOptions) { genre ->
                FilterChip(
                    selected = selectedGenre == genre,
                    onClick = { viewModel.selectGenre(genre) },
                    label = { Text(genre) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("chip_genre_$genre")
                )
            }
        }

        // Add game prompt banner / FAB
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Catálogo (${games.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("btn_add_game")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }

        // Game Catalog Grid
        if (games.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Gamepad,
                title = "Nenhum jogo encontrado",
                subtitle = "Tente alterar os filtros ou clique em 'Adicionar' para cadastrar um novo jogo!",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("game_grid")
            ) {
                items(games, key = { it.id }) { game ->
                    GameCatalogCard(
                        game = game,
                        onClick = { onGameClicked(game) },
                        onToggleWishlist = { viewModel.toggleWishlist(game) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGameDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, plat, gen, year, dev, desc, wishlist ->
                viewModel.addNewGame(title, plat, gen, year, dev, desc, wishlist)
            }
        )
    }
}

@Composable
fun GameCatalogCard(
    game: Game,
    onClick: () -> Unit,
    onToggleWishlist: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                1.dp,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .testTag("game_card_${game.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Genre badge & Platform text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = game.genre,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Platform & Developer
                Text(
                    text = game.platform,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating stars summary
                if (game.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarActive,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${game.rating.toInt()}/5",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StarActive
                        )
                        if (!game.review.isNullOrEmpty()) {
                            Icon(
                                imageVector = Icons.Rounded.Comment,
                                contentDescription = "Possui resenha",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Sem nota",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Wishlist Toggle Top Right Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.size(36.dp).testTag("btn_wish_toggle_${game.id}")
                ) {
                    Icon(
                        imageVector = if (game.isWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Lista de desejos",
                        tint = if (game.isWishlist) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onGameClicked: (Game) -> Unit
) {
    val reviewedGames by viewModel.reviewedGames.collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Minhas Resenhas (${reviewedGames.size})",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (reviewedGames.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.RateReview,
                title = "Nenhuma resenha ainda",
                subtitle = "Toque em qualquer jogo do catálogo e adicione sua nota e comentários sobre ele!",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("reviews_grid")
            ) {
                items(reviewedGames, key = { it.id }) { game ->
                    ReviewCard(
                        game = game,
                        onClick = { onGameClicked(game) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    game: Game,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .testTag("review_card_${game.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${game.genre} • ${game.platform}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${game.rating.toInt()}/5",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stars graphic
            StarRatingBar(
                rating = game.rating,
                starSize = 18
            )

            // Review date
            if (game.reviewDate > 0L) {
                Text(
                    text = "Avaliado em ${formatReviewDate(game.reviewDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Review message bubble
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = if (game.review.isNullOrBlank()) "Sem resenha escrita." else game.review,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (game.review.isNullOrBlank()) FontWeight.Light else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun WishlistScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onGameClicked: (Game) -> Unit
) {
    val wishlistGames by viewModel.wishlistGames.collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Lista de Desejos (${wishlistGames.size})",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (wishlistGames.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Sua wishlist está vazia",
                subtitle = "Visite o catálogo de jogos e toque no ícone de coração para salvar os jogos que você deseja jogar!",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("wishlist_grid")
            ) {
                items(wishlistGames, key = { it.id }) { game ->
                    WishlistCard(
                        game = game,
                        onClick = { onGameClicked(game) },
                        onRemove = { viewModel.toggleWishlist(game) }
                    )
                }
            }
        }
    }
}

@Composable
fun WishlistCard(
    game: Game,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                1.dp,
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .testTag("wishlist_card_${game.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${game.genre} • ${game.platform}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (game.releaseYear.isNotEmpty() || game.developer.isNotEmpty()) {
                    Text(
                        text = "${game.developer} (${game.releaseYear})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(44.dp).testTag("btn_remove_wish_${game.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover da lista de desejos",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ---------------- DIALOGS ----------------

@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, platform: String, genre: String, releaseYear: String, developer: String, description: String, isWishlist: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var developer by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("PC") }
    var selectedGenre by remember { mutableStateOf("RPG") }
    var description by remember { mutableStateOf("") }
    var isWishlist by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf(false) }

    val platformOptions = listOf("PC", "PS5", "Xbox", "Switch", "Mobile", "Multiplataforma")
    val genreOptions = listOf("RPG", "Ação", "Aventura", "Simulação", "Roguelike", "Estratégia", "Esportes", "Terror", "Outro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Cadastrar Novo Jogo",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.trim().isBlank()) {
                        titleError = true
                    } else {
                        onSave(title.trim(), selectedPlatform, selectedGenre, releaseYear.trim(), developer.trim(), description.trim(), isWishlist)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("dialog_btn_add")
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Título do Jogo *") },
                    isError = titleError,
                    placeholder = { Text("Ex: Elden Ring") },
                    modifier = Modifier.fillMaxWidth().testTag("input_title"),
                    shape = RoundedCornerShape(8.dp)
                )
                if (titleError) {
                    Text(
                        text = "O título é obrigatório",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = developer,
                    onValueChange = { developer = it },
                    label = { Text("Desenvolvedor / Estúdio") },
                    placeholder = { Text("Ex: FromSoftware") },
                    modifier = Modifier.fillMaxWidth().testTag("input_developer"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it },
                    label = { Text("Ano de Lançamento") },
                    placeholder = { Text("Ex: 2022") },
                    modifier = Modifier.fillMaxWidth().testTag("input_year"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text(
                    "Plataforma Principal:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(platformOptions) { platform ->
                        FilterChip(
                            selected = selectedPlatform == platform,
                            onClick = { selectedPlatform = platform },
                            label = { Text(platform) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("dialog_plat_$platform")
                        )
                    }
                }

                Text(
                    "Gênero:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(genreOptions) { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = genre },
                            label = { Text(genre) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("dialog_genre_$genre")
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do Jogo") },
                    placeholder = { Text("Fale brevemente sobre o jogo...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_description"),
                    maxLines = 4,
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isWishlist = !isWishlist }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isWishlist,
                        onCheckedChange = { isWishlist = it },
                        modifier = Modifier.testTag("check_dialog_wishlist")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adicionar à Lista de Desejos", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    )
}

@Composable
fun ReviewDetailsDialog(
    game: Game,
    onDismiss: () -> Unit,
    onSaveReview: (rating: Float, review: String) -> Unit,
    onToggleWishlist: () -> Unit,
    onDeleteGame: () -> Unit,
    onRemoveReview: () -> Unit
) {
    var rating by remember { mutableStateOf(if (game.rating > 0f) game.rating else 0.0f) }
    var reviewText by remember { mutableStateOf(game.review ?: "") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        confirmButton = {
            Button(
                onClick = {
                    onSaveReview(rating, reviewText)
                    onDismiss()
                },
                modifier = Modifier.testTag("dialog_review_btn_save")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Voltar")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Game header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (game.developer.isNotEmpty() || game.releaseYear.isNotEmpty()) {
                            Text(
                                text = "${game.developer} (${game.releaseYear})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier.testTag("btn_detail_wish")
                    ) {
                        Icon(
                            imageVector = if (game.isWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar / Desejos",
                            tint = if (game.isWishlist) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Genre & Platform tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(game.platform) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(game.genre) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    )
                }

                // Description
                if (game.description.isNotEmpty()) {
                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // User Review section
                Text(
                    text = "Sua Avaliação",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Large Interactive stars
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StarRatingBar(
                        rating = rating,
                        onRatingChanged = { rating = it },
                        starSize = 36
                    )

                    if (rating > 0) {
                        Text(
                            text = "Nota: ${rating.toInt()} / 5 estrelas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StarActive
                        )
                    } else {
                        Text(
                            text = "Sem nota — Toque acima para avaliar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Resenha / Comentários") },
                    placeholder = { Text("Escreva aqui o que você achou dos gráficos, jogabilidade, narrativa e diversão do jogo...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_review_text"),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (game.rating > 0 || !game.review.isNullOrEmpty()) {
                        TextButton(
                            onClick = {
                                onRemoveReview()
                                onDismiss()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.testTag("btn_clear_review")
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excluir Avaliação", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.testTag("btn_delete_game_all")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Deletar Jogo", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Deletar jogo?") },
            text = { Text("Isso removerá permanentemente o jogo '${game.title}' e todas as resenhas associadas. Deseja continuar?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteGame()
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("dialog_btn_confirm_delete")
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
