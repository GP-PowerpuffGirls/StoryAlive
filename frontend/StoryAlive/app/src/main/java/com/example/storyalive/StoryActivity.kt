package com.example.storyalive

import android.media.MediaPlayer
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.model.StoryRequestDTO
import com.example.storyalive.model.StoryResponseDTO
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.themeColors
import com.google.gson.Gson
import androidx.compose.material.icons.outlined.Pause

class StoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storyJson = intent.getStringExtra("STORY_JSON")
        requireNotNull(storyJson)
        val story = Gson().fromJson(storyJson, StoryResponseDTO::class.java)

        enableEdgeToEdge()
        setContent {
            var isLightTheme by remember { mutableStateOf(true) }

            StoryAliveTheme(darkTheme = !isLightTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column {
                            StoryAliveTopBar(selectedPage = "Published")
                            StoryDetailScreen(
                                story = story,
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
    story: StoryResponseDTO,
    isLightTheme: Boolean = true
) {

    val colors = themeColors(isLightTheme)
    val scrollState = rememberScrollState()

    var playbackPosition by remember { mutableFloatStateOf(0.15f) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    val context = LocalContext.current

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    LaunchedEffect(mediaPlayer) {
        while (true) {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    currentPosition = it.currentPosition.toFloat()
                }
            }
            kotlinx.coroutines.delay(500)
        }
    }
    LaunchedEffect(Unit) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                setDataSource(story.finalAudioPath) // 🔥 your backend audio
                prepareAsync()

                setOnPreparedListener {
                    // ready to play
                }

                setOnCompletionListener {
                    isPlaying = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
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
                    text = story.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                val formattedDate = remember {
                    try {
                        val instant = java.time.Instant.parse(story.createdAt)
                        val date = java.util.Date.from(instant)
                        java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                            .format(date)
                    } catch (e: Exception) {
                        "Unknown date"
                    }
                }

                Text(
                    text = "Uploaded on $formattedDate",
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
                            value = if (mediaPlayer != null && mediaPlayer!!.duration > 0) {
                                currentPosition / mediaPlayer!!.duration
                            } else 0f,
                            onValueChange = {
                                mediaPlayer?.let { player ->
                                    val newPosition = (it * player.duration).toInt()
                                    player.seekTo(newPosition)
                                }
                            },
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
                            Text(formatDuration((currentPosition /1000).toDouble()), fontSize = 12.sp, color = colors.muted)
                            Text(formatDuration(story.duration), fontSize = 12.sp, color = colors.muted)
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
                                    .clickable {
                                        mediaPlayer?.let { player ->
                                            if (player.isPlaying) {
                                                player.pause()
                                                isPlaying = false
                                            } else {
                                                player.start()
                                                isPlaying = true
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying)
                                        Icons.Outlined.Pause
                                    else
                                        Icons.Outlined.PlayArrow,
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
                                onValueChange = {
                                    volume = it
                                    mediaPlayer?.setVolume(it, it)
                                },
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
                                text = "${(volume * 100).toInt()}%",
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
                    text = story.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = colors.text
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}
fun formatDuration(seconds: Double): String {
    val mins = (seconds / 60).toInt()
    val secs = (seconds % 60).toInt()
    return "%d:%02d".format(mins, secs)
}
//@Preview(showBackground = true, name = "Light Mode - Story Detail")
//@Composable
//fun StoryDetailPreviewLight() {
//
//
//    StoryAliveTheme(darkTheme = false) {
//        Column {
//            StoryAliveTopBar(selectedPage = "Published")
//            StoryDetailScreen(
//                story = fakeStory,
//                isLightTheme = true
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "Dark Mode - Story Detail")
//@Composable
//fun StoryDetailPreviewDark() {
//    val fakeStory = Story(
//        title = "The Adventure Begins",
//        description = "This is a sample story for preview.",
//        createdAt = java.util.Date().toInstant(),
//        duration = 120.0,
//        finalAudioPath = ""
//    )
//
//    StoryAliveTheme(darkTheme = true) {
//        Column {
//            StoryAliveTopBar(selectedPage = "Published")
//            StoryDetailScreen(
//                story = fakeStory,
//                isLightTheme = false
//            )
//        }
//    }
//}