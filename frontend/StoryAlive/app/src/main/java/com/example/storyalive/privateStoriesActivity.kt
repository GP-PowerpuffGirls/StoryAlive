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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
class privateStoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isLightTheme by remember { mutableStateOf(true) }

            StoryAliveTheme(darkTheme = !isLightTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        PrivateStoriesScreen(
                            isLightTheme = isLightTheme,
                            onStoryClick = { title, date ->
                                // 5. Start StoryActivity via Intent
                                val intent = android.content.Intent(this@privateStoriesActivity, StoryActivity::class.java).apply {
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
@Composable
fun PrivateStoriesScreen(isLightTheme: Boolean = true,onStoryClick: (String, String) -> Unit) {
    val colors = themeColors(isLightTheme)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Private Stories",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Text(
                    text = "Stories that are kept private and not published to the community",
                    fontSize = 14.sp,
                    color = colors.muted
                )
            }

            // Private Count Badge
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
                    Text("1 Private", fontSize = 14.sp, color = colors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Grid of Stories ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // Fixed(1) matches your image better than Fixed(2)
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                PrivateStoryCard(colors=colors,onClick = { onStoryClick("My Private Story", "3/11/2026") })
            }
        }
    }
}

@Composable
fun PrivateStoryCard(colors: com.example.storyalive.ui.theme.ThemeColors,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Matches the wide look in the image
            .wrapContentHeight()
            .clickable() { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Image Section with Overlay
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                // REAL IMAGE LOADING
                // Replace the URL with your story's image link
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1544947950-fa07a98d237f",
                    contentDescription = "Story Thumbnail",
                    contentScale = ContentScale.Crop, // This ensures the image fills the space
                    modifier = Modifier.fillMaxSize()
                )

                // Yellow Private Badge Overlay
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

            // Content Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Private Story",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Text(
                    text = "A personal story kept private.",
                    fontSize = 14.sp,
                    color = colors.muted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Tags Row
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    TagChip("Drama", colors.accent, Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    TagChip("Drama", Color.LightGray.copy(0.3f), colors.muted)
                    Spacer(modifier = Modifier.width(6.dp))
                    TagChip("Inspirational", Color.LightGray.copy(0.3f), colors.muted)
                }

                // Bottom Row (Time and Age)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = colors.muted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("30:20", fontSize = 12.sp, color = colors.muted)
                    }
                    Text("Age 13+", fontSize = 12.sp, color = colors.muted)
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
@Preview(showBackground = true, name = "Light Mode - Private Stories")
@Composable
fun PrivateStoriesPreviewLight() {
    StoryAliveTheme(darkTheme = false) {
        // Mocking the screen in Light Mode
        PrivateStoriesScreen(isLightTheme = true,onStoryClick = { _, _ -> })
    }
}

@Preview(showBackground = true, name = "Dark Mode - Private Stories")
@Composable
fun PrivateStoriesPreviewDark() {
    StoryAliveTheme(darkTheme = true) {
        // Mocking the screen in Dark Mode
        PrivateStoriesScreen(isLightTheme = false,onStoryClick = { _, _ -> })
    }
}