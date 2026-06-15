package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Game
import com.example.ui.GameViewModel
import com.example.ui.GameViewModelFactory
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ReviewDetailsDialog
import com.example.ui.screens.ReviewsScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var selectedTab by remember { mutableStateOf(0) }
                var activeGameForDetail by remember { mutableStateOf<Game?>(null) }

                // Keep detail dialog current if the database flow updates activeGameForDetail properties
                var detailGameId by remember { mutableStateOf<Int?>(null) }
                val allGamesRaw = viewModel.allGames.collectAsState(initial = emptyList())
                
                LaunchedEffect(activeGameForDetail, allGamesRaw.value) {
                    val currentId = activeGameForDetail?.id
                    if (currentId != null) {
                        val matchingGame = allGamesRaw.value.find { it.id == currentId }
                        if (matchingGame != null) {
                            activeGameForDetail = matchingGame
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gamepad,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "QuestLog",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier.testTag("app_bar")
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("bottom_nav")
                                .windowInsetsPadding(WindowInsets.navigationBars) // properly handling notch inset
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Gamepad,
                                        contentDescription = "Catálogo"
                                    )
                                },
                                label = { Text("Catálogo") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFEADDFF),
                                    unselectedIconColor = Color(0xFFCAC4D0),
                                    selectedTextColor = Color(0xFFE6E1E5),
                                    unselectedTextColor = Color(0xFFCAC4D0),
                                    indicatorColor = Color(0xFF4F378B)
                                ),
                                modifier = Modifier.testTag("tab_library")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.RateReview,
                                        contentDescription = "Resenhas"
                                    )
                                },
                                label = { Text("Resenhas") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFEADDFF),
                                    unselectedIconColor = Color(0xFFCAC4D0),
                                    selectedTextColor = Color(0xFFE6E1E5),
                                    unselectedTextColor = Color(0xFFCAC4D0),
                                    indicatorColor = Color(0xFF4F378B)
                                ),
                                modifier = Modifier.testTag("tab_reviews")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Favorite,
                                        contentDescription = "Desejos"
                                    )
                                },
                                label = { Text("Desejos") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFEADDFF),
                                    unselectedIconColor = Color(0xFFCAC4D0),
                                    selectedTextColor = Color(0xFFE6E1E5),
                                    unselectedTextColor = Color(0xFFCAC4D0),
                                    indicatorColor = Color(0xFF4F378B)
                                ),
                                modifier = Modifier.testTag("tab_wishlist")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                (fadeIn() + slideInHorizontally { width -> if (targetState > initialState) width else -width })
                                    .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState > initialState) -width else width })
                            },
                            label = "tab_fade"
                        ) { targetTab ->
                            when (targetTab) {
                                0 -> LibraryScreen(
                                    viewModel = viewModel,
                                    onGameClicked = { game -> activeGameForDetail = game }
                                )
                                1 -> ReviewsScreen(
                                    viewModel = viewModel,
                                    onGameClicked = { game -> activeGameForDetail = game }
                                )
                                2 -> WishlistScreen(
                                    viewModel = viewModel,
                                    onGameClicked = { game -> activeGameForDetail = game }
                                )
                            }
                        }
                    }
                }

                // Game and review details overlay details
                activeGameForDetail?.let { game ->
                    ReviewDetailsDialog(
                        game = game,
                        onDismiss = { activeGameForDetail = null },
                        onSaveReview = { rating, text ->
                            viewModel.submitReview(game, rating, text)
                        },
                        onToggleWishlist = {
                            viewModel.toggleWishlist(game)
                        },
                        onDeleteGame = {
                            viewModel.deleteGame(game)
                            activeGameForDetail = null
                        },
                        onRemoveReview = {
                            viewModel.deleteReview(game)
                        }
                    )
                }
            }
        }
    }
}
