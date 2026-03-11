package com.example.storyalive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.themeColors

class StoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve data passed from HistoryActivity
        val storyTitle = intent.getStringExtra("STORY_TITLE") ?: "Unknown Story"
        val storyDate = intent.getStringExtra("STORY_DATE") ?: "Unknown Date"

        enableEdgeToEdge()
        setContent {
            var isLightTheme by remember { mutableStateOf(true) }

            StoryAliveTheme(darkTheme = !isLightTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column {
                            StoryAliveTopBar(selectedPage = "Published")
                            StoryDetailScreen(
                                title = storyTitle,
                                date = storyDate,
                                isLightTheme = isLightTheme
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    title: String,
    date: String,
    isLightTheme: Boolean = true
) {

    val colors = themeColors(isLightTheme)
    val scrollState = rememberScrollState()

    var playbackPosition by remember { mutableFloatStateOf(0.15f) }
    var volume by remember { mutableFloatStateOf(0.8f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = colors.card),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )

                Text(
                    text = "Uploaded on $date",
                    fontSize = 13.sp,
                    color = colors.muted
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.background.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, Color.LightGray.copy(.25f))
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // PROGRESS BAR

                        Slider(
                            value = playbackPosition,
                            onValueChange = { playbackPosition = it },
                            colors = SliderDefaults.colors(
                                // We make both tracks the same light color to match the image
                                activeTrackColor = Color.LightGray.copy(alpha = 0.3f),
                                inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f),
                                // This hides the default solid thumb so our custom one shows
                                thumbColor = Color.Transparent
                            ),
                            thumb = {
                                // Custom hollow circle thumb
                                Surface(
                                    modifier = Modifier.size(20.dp),
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(2.dp, Color.Gray) // The dark ring
                                ) {}
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0:00", fontSize = 12.sp, color = colors.muted)
                            Text("6:12", fontSize = 12.sp, color = colors.muted)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // CONTROLS

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(28.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.SkipPrevious,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { },
                                tint = colors.muted
                            )

                            // PLAY BUTTON

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(colors.accent, CircleShape)
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )

                            }

                            Icon(
                                imageVector = Icons.Outlined.SkipNext,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { },
                                tint = colors.muted
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // SPEED

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = "Playback Speed: 1x",
                                fontSize = 13.sp,
                                color = colors.muted
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp), // Adjusted spacing
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                listOf(
                                    "0.5x",
                                    "0.75x",
                                    "1x",
                                    "1.25x",
                                    "1.5x",
                                    "2x"
                                ).forEach { speed ->

                                    val selected = speed == "1x"

                                    Box(
                                        modifier = Modifier
                                            .border(
                                                1.dp,
                                                if (selected) colors.accent else Color.Gray,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .background(
                                                if (selected) colors.accent else Color.Transparent,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {

                                        Text(
                                            text = speed,
                                            fontSize = 10.sp,
                                            color = if (selected) Color.White else colors.heading
                                        )

                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // VOLUME

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.VolumeUp,
                                contentDescription = null,
                                tint = colors.muted,
                                modifier = Modifier.size(20.dp)
                            )

                            val sliderColors = SliderDefaults.colors(
                                activeTrackColor = colors.accent,
                                inactiveTrackColor = Color.LightGray.copy(alpha = 0.4f),
                                thumbColor = Color.Transparent
                            )

                            Slider(
                                value = volume,
                                onValueChange = { volume = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = sliderColors,
                                thumb = {
                                    Surface(
                                        modifier = Modifier.size(18.dp),
                                        shape = CircleShape,
                                        color = Color.White,
                                        border = BorderStroke(1.5.dp, Color.DarkGray.copy(alpha = 0.7f))
                                    ) {}
                                },
                                track = { sliderState ->
                                    SliderDefaults.Track(
                                        sliderState = sliderState,
                                        modifier = Modifier.height(4.dp),
                                        colors = sliderColors, // Pass the colors object here instead of individual colors
                                        enabled = true
                                    )
                                }
                            )
                            Text(
                                text = "100%",
                                fontSize = 12.sp,
                                color = colors.muted
                            )
                        }

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // STORY CARD

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.card),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Story Content",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text =
                        "Once upon a time, in a land far, far away, there lived a young adventurer named Alex. Alex had always dreamed of exploring the mysterious forests that lay beyond the village.\n\nOne bright morning, Alex packed a small bag with essentials—a map, some food, and a compass that had belonged to their grandfather.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = colors.text
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Preview(showBackground = true, name = "Light Mode - Story Detail")
@Composable
fun StoryDetailPreviewLight() {
    StoryAliveTheme(darkTheme = false) {
        Column {
            StoryAliveTopBar(selectedPage = "Published")
            // Mocking the screen with sample data
            StoryDetailScreen(
                title = "The Adventure Begins",
                date = "2/20/2026",
                isLightTheme = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode - Story Detail")
@Composable
fun StoryDetailPreviewDark() {
    StoryAliveTheme(darkTheme = true) {
        Column {
            StoryAliveTopBar(selectedPage = "Published")
            // Mocking the screen in Dark Mode
            StoryDetailScreen(
                title = "The Adventure Begins",
                date = "2/20/2026",
                isLightTheme = false
            )
        }
    }
}