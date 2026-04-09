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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.themeColors
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.model.StoryResponseDTO
import com.example.storyalive.network.RetrofitClient
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.google.gson.Gson


class privateStoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isLightTheme by remember { mutableStateOf(true) }

            StoryAliveTheme(darkTheme = !isLightTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column {
                            StoryAliveTopBar(selectedPage = "Private")
                            PrivateStoriesScreen(
                                isLightTheme = isLightTheme,
                                onStoryClick = { story ->
                                    val intent = android.content.Intent(
                                        this@privateStoriesActivity,
                                        StoryActivity::class.java
                                    ).apply {
                                        putExtra("STORY_JSON", Gson().toJson(story)) // ✅ الصح
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
fun PrivateStoriesScreen(isLightTheme: Boolean = true,onStoryClick: (StoryResponseDTO) -> Unit) {
    val colors = themeColors(isLightTheme)
    val context = androidx.compose.ui.platform.LocalContext.current
    val api = remember { RetrofitClient.createApi(context) }

    var privateStories by remember { mutableStateOf<List<StoryResponseDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(1) }

    val listState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    fun formatDuration(durationInHours: Double): String {
        val totalSeconds = (durationInHours * 3600).toInt() // convert hours to seconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }


    // Function to fetch private stories
    suspend fun loadPrivateStories(page: Int) {
        try {
            isLoadingMore = true
            val response = api.getPrivateStories(pageNumber = page, pageSize = 10)
            if (response.isSuccessful) {
                val content = response.body()?.content ?: emptyList()

                if (page == 0) privateStories = content
                else privateStories = privateStories +  content

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
    LaunchedEffect(Unit) { loadPrivateStories(0) }

    // Infinite scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (!isLoadingMore && !isLoading && index >= privateStories.size - 3 && currentPage + 1 < totalPages) {
                    currentPage++
                    loadPrivateStories(currentPage)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Private Stories", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.heading)
                Text(
                    "Stories that are kept private and not published to the community",
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
                    Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.muted)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${privateStories.size} Private", fontSize = 14.sp, color = colors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading && privateStories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(privateStories.size) { index ->
                    val story = privateStories[index]
                    PrivateStoryCard(colors = colors,story=story, onClick = { onStoryClick(story) })
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivateStoryCard(
    colors: com.example.storyalive.ui.theme.ThemeColors,
    onClick: () -> Unit,
    story: StoryResponseDTO
) {
    val durationText = remember(story.duration) {
        formatDuration(story.duration)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Title at the very top
                Text(
                    story.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading,
                    modifier = Modifier.padding(top = 8.dp) // optional, small space
                )

                // Description
                Text(
                    story.description,
                    fontSize = 14.sp,
                    color = colors.muted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Tags
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    story.tags.forEach { tag ->
                        TagChip(tag, colors.accent, Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = colors.muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(durationText, fontSize = 12.sp, color = colors.muted)
                    }
                    Text("Age ${story.minimumAge}+", fontSize = 12.sp, color = colors.muted)
                }
            }

            // Overlay badge (does not push the Column down)
            Surface(
                color = Color(0xFF2D2D2D).copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Private",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun TagChip(text: String, bgColor: Color, textColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Medium
        )
    }
}
//@Preview(showBackground = true, name = "Light Mode - Private Stories")
//@Composable
//fun PrivateStoriesPreviewLight() {
//    StoryAliveTheme(darkTheme = false) {
//        Column {
//            StoryAliveTopBar(selectedPage = "Private")
//            // Mocking the screen in Light Mode
//            PrivateStoriesScreen(isLightTheme = true, onStoryClick = { _, _ -> })
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "Dark Mode - Private Stories")
//@Composable
//fun PrivateStoriesPreviewDark() {
//    StoryAliveTheme(darkTheme = true) {
//        Column {
//            StoryAliveTopBar(selectedPage = "Private")
//            // Mocking the screen in Dark Mode
//            PrivateStoriesScreen(isLightTheme = false, onStoryClick = { _, _ -> })
//        }
//    }
//}