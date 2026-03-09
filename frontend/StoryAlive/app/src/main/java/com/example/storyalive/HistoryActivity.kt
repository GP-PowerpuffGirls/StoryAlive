package com.example.storyalive

import android.os.Bundle
import android.widget.Space
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StoryAliveTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "history") {
                    composable("history") {
                        HistoryScreen(onStoryClick = { title ->
                            navController.navigate("detail/$title")
                        })
                    }
                    composable("detail/{title}") { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title") ?: ""
                        StoryDetailScreen(title = title, date = "2/20/2026")
                    }
                }
            }
        }
    }
}
data class StoryHistoryItem(
    val title: String,
    val date: String,
    val duration: String,
    val hasBgm: Boolean = false,
    val hasSfx: Boolean = false
)
@Composable
fun HistoryScreen(isLightTheme: Boolean=true,onStoryClick: (String) -> Unit){
    val colors= themeColors(isLightTheme)
    val historyList =listOf(
        StoryHistoryItem("The Adventure Begins", "2/20/2026", "5:32", hasBgm = true),
        StoryHistoryItem("Mystery of the Lost City", "2/18/2026", "8:15", hasBgm = true, hasSfx = true),
        StoryHistoryItem("The Enchanted Garden", "2/15/2026", "6:48", hasSfx = true)
    )
    Column(modifier = Modifier.fillMaxSize().background(colors.background).padding(16.dp)) {
        Text(
            text = "Your Story History",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.heading,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(historyList){
                story->StoryCard(story,colors,onNavigate = { onStoryClick(story.title) })
            }
        }
    }
}
@Composable
fun StoryCard(story: StoryHistoryItem,colors: com.example.storyalive.ui.theme.ThemeColors,onNavigate:()-> Unit){
    Card(
        colors= CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable{onNavigate()},
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(8.dp))
                //Date and Duration
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text=story.date, color = colors.muted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.PlayCircleOutline,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = story.duration,color=colors.muted, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                //Tags (BGM/SFX)
                Row{
                    if (story.hasBgm){
                        TagBadge(text="Background Music",icon= Icons.Outlined.MusicNote,colors=colors)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (story.hasSfx){
                        TagBadge(text="SFX",icon= Icons.Outlined.VolumeUp,colors=colors)
                    }

                }
            }
            //Play Button
            FilledIconButton(
                onClick = onNavigate,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.accent)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
    }
}
@Composable
fun TagBadge(text: String, icon: ImageVector, colors: com.example.storyalive.ui.theme.ThemeColors) {
    Surface(
        color = colors.background.copy(alpha = 0.5f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.muted.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = colors.text.copy(alpha = 0.8f)
            )
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
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NavigationFlowPreview() {
    StoryAliveTheme {
        // Create a local navController for the preview
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "history") {
            composable("history") {
                HistoryScreen(onStoryClick = { title ->
                    navController.navigate("detail/$title")
                })
            }
            composable("detail/{title}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                StoryDetailScreen(title = title, date = "2/20/2026")
            }
        }
    }
}
@Preview(showBackground = true, name = "Light Mode", showSystemUi = true)
@Composable
fun HistoryPreviewLight() {
    StoryAliveTheme {
        // We pass an empty lambda {} because we don't need actual navigation in the preview
        HistoryScreen(
            isLightTheme = true,
            onStoryClick = { title -> /* Do nothing in preview */ }
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode", showSystemUi = true)
@Composable
fun HistoryPreviewDark() {
    StoryAliveTheme {
        HistoryScreen(
            isLightTheme = false,
            onStoryClick = { title -> /* Do nothing in preview */ }
        )
    }
}