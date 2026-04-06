package com.example.storyalive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.model.StoryResponseDTO
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.themeColors

class FavoriteStoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isLightTheme by remember { mutableStateOf(true) }
            StoryAliveTheme(darkTheme = !isLightTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column {
                            StoryAliveTopBar(selectedPage = "Favorites")
                            FavoriteStoriesScreen(
                                isLightTheme = isLightTheme,
                                onStoryClick = { title, date ->
                                    // Use Intent to move to StoryActivity
                                    val intent = android.content.Intent(
                                        this@FavoriteStoriesActivity,
                                        StoryActivity::class.java
                                    ).apply {
                                        putExtra("STORY_TITLE", title)
                                        putExtra("STORY_DATE", date)
                                    }
                                    startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteStoriesScreen(isLightTheme: Boolean = true, onStoryClick: (String, String) -> Unit) {
    val colors = themeColors(isLightTheme)
    val context = androidx.compose.ui.platform.LocalContext.current
    val api = remember { RetrofitClient.createApi(context) }

    var favoriteStories by remember { mutableStateOf<List<StoryResponseDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }

    var currentPage by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(1) }

    val listState = rememberLazyListState()

    // Function to fetch favorites from API
    suspend fun loadFavorites(page: Int) {
        try {
            isLoadingMore = true
            val response = api.getFavorites(pageNumber = page, pageSize = 10) // Adjust page size
            if (response.isSuccessful) {
                val content = response.body()?.content ?: emptyList()

                if (page == 0) favoriteStories = content
                else favoriteStories = favoriteStories + content
                totalPages = response.body()?.totalPages ?: 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
            isLoadingMore = false
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        loadFavorites(0)
    }

    // Infinite scroll for pagination
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (!isLoadingMore && !isLoading && index >= favoriteStories.size - 3 && currentPage + 1 < totalPages) {
                    currentPage++
                    loadFavorites(currentPage)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Favorite Stories",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Text(
                    "Your collection of favorite stories from the community",
                    fontSize = 14.sp,
                    color = colors.muted
                )
            }

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(0.5f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${favoriteStories.size} Favorites",
                        fontSize = 14.sp,
                        color = colors.muted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (isLoading && favoriteStories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(favoriteStories) { story ->
                    FavoriteStoryCard(
                        story = story,
                        colors = colors,
                        onClick = { onStoryClick(story.title, "Unknown Date") }
                    )
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator()
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun FavoriteStoryCard(
    story: StoryResponseDTO,
    colors: com.example.storyalive.ui.theme.ThemeColors,
    onClick: () -> Unit
) {
    val durationText = remember(story.duration) {
        val minutes = story.duration.toInt()
        val seconds = ((story.duration - minutes) * 60).toInt()
        "%02d:%02d".format(minutes, seconds)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Heart Icon Overlay
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(28.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    story.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )

                Text(
                    story.description,
                    fontSize = 14.sp,
                    color = colors.muted,
                    modifier = Modifier.padding(vertical = 8.dp),
                    lineHeight = 20.sp
                )

                // Tags
                Row(modifier = Modifier.padding(bottom = 12.dp)) {
                    story.tags.forEachIndexed { index, tag ->
                        TagChip(
                            text = tag,
                            bgColor = if (index == 0) colors.accent else Color.LightGray.copy(0.3f),
                            textColor = if (index == 0) Color.White else colors.muted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.AccessTime,
                            null,
                            tint = colors.muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(durationText, fontSize = 12.sp, color = colors.muted)
                        Icon(
                            Icons.Outlined.Visibility,
                            null,
                            tint = colors.muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("${story.numberOfViews} views", fontSize = 12.sp, color = colors.muted)
                    }
                    Text("Age ${story.minimumAge}+", fontSize = 12.sp, color = colors.muted)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode - Favorites")
@Composable
fun FavoriteStoriesPreviewLight() {
    StoryAliveTheme(darkTheme = false) {
        Column {
            StoryAliveTopBar(selectedPage = "Favorites")
            // Passing true for isLightTheme to use your light color palette
            FavoriteStoriesScreen(isLightTheme = true, onStoryClick = { title, date -> })
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode - Favorites")
@Composable
fun FavoriteStoriesPreviewDark() {
    StoryAliveTheme(darkTheme = true) {
        Column {
            StoryAliveTopBar(selectedPage = "Favorites")
            // Passing false for isLightTheme to use your dark color palette
            FavoriteStoriesScreen(isLightTheme = false, onStoryClick = { title, date -> })
        }
    }
}