package com.example.storyalive

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import com.example.storyalive.model.RequestStoryUpdateDTO
import com.example.storyalive.model.Sentence
import com.example.storyalive.model.StoryResponseDTO
import com.example.storyalive.model.TimedSentence
import com.example.storyalive.model.TranscriptResponse
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.themeColors
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import kotlin.collections.indexOfFirst


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

fun flattenSentences(data: TranscriptResponse): List<Sentence> {
    return data.chapters
        .flatMap { it.scenes }
        .flatMap { it.sentences }
}

fun generateTimedSentences(
    sentences: List<Sentence>,
    totalDuration: Double
): List<TimedSentence> {

    val totalWords = sentences.sumOf { it.sentence.split(" ").size }
    var currentTime = 0.0

    return sentences.map { s ->

        val wordCount = s.sentence.split(" ").size
        val ratio = wordCount.toDouble() / totalWords
        val duration = ratio * totalDuration

        val timed = TimedSentence(
            sentenceId = s.sentenceId,
            text = s.sentence,
            speaker = s.speaker,
            start = currentTime,
            end = currentTime + duration
        )

        currentTime += duration
        timed
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    story: StoryResponseDTO,
    isLightTheme: Boolean = true
) {
    val colors = themeColors(isLightTheme)
    var timedTranscript by remember { mutableStateOf<List<TimedSentence>>(emptyList()) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var isReady by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentSentenceIndex = remember(currentPosition, timedTranscript) {
        val currentSec = currentPosition / 1100.0
        timedTranscript.indexOfFirst { currentSec in it.start..it.end }
    }
    var selectedSentence by remember {
        mutableStateOf<TimedSentence?>(null)
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var selectedEmotion by remember { mutableStateOf("NARRATION") }
    var selectedIntensity by remember { mutableStateOf("LOW") }
    var emotions by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(currentSentenceIndex) {
        if (currentSentenceIndex != -1) {
            listState.animateScrollToItem(currentSentenceIndex)
        }
    }
    //MEDIA PLAYER
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            mediaPlayer?.let {
                currentPosition = it.currentPosition.toFloat()
            }
            kotlinx.coroutines.delay(100)
        }
    }
    //LOAD AUDIO
    LaunchedEffect(Unit) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                setDataSource(story.finalAudioPath) // 🔥 your backend audio
                prepareAsync()

                setOnPreparedListener {
                    isReady = true
                }

                setOnCompletionListener {
                    isPlaying = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //LOAD TRANSCRIPT
    LaunchedEffect(story.jsonPath) {
        try {
            val json = withContext(kotlinx.coroutines.Dispatchers.IO) {
                java.net.URL(story.jsonPath).openStream().bufferedReader().use { it.readText() }
            }
            Log.d("StoryDebug", "RAW JSON START: $json")
            val data = Gson().fromJson(json, TranscriptResponse::class.java)

            val sentences = flattenSentences(data)

            timedTranscript = generateTimedSentences(
                sentences,
                story.duration // 🔥 use audio duration
            )
            Log.d("StoryDebug", "Loaded sentences: ${timedTranscript.size}")

        } catch (e: Exception) {
            Log.e("StoryDebug", "Transcript load failed", e)
        }
    }
    LaunchedEffect(Unit) {
        try {
            val enums = RetrofitClient
                .createApi(context)
                .getEnums()

            Log.d("ENUMS", enums.toString())

            emotions =
                enums["emotions"]
                    ?: enums["EMOTIONS"]
                                ?: emptyList()

        } catch (e: Exception) {
            Log.e("ENUMS", "Failed to load enums", e)
        }
    }
    //UI
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // ================= PLAYER CARD =================
        item {
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
                        border = BorderStroke(1.dp, Color(0xFF917A73).copy(.25f))
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // PROGRESS BAR

                            Slider(
                                value = if (mediaPlayer != null && isReady && mediaPlayer!!.duration > 0) {
                                    currentPosition / mediaPlayer!!.duration
                                } else 0f,
                                onValueChange = {
                                    if (!isReady) return@Slider
                                    mediaPlayer?.seekTo((it * mediaPlayer!!.duration).toInt())
                                },
                                colors = SliderDefaults.colors(
                                    // We make both tracks the same light color to match the image
                                    activeTrackColor = colors.accent,
                                    inactiveTrackColor = Color(0xFF917A73).copy(alpha = 0.3f),
                                    // This hides the default solid thumb so our custom one shows
                                    thumbColor = Color.Transparent
                                ),
                                thumb = {
                                    // Custom hollow circle thumb
                                    Surface(
                                        modifier = Modifier.size(20.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFFFF0D1),
                                        border = BorderStroke(2.dp, colors.accent) // The dark ring
                                    ) {}
                                },
                                modifier = Modifier.fillMaxWidth()
                            )


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    formatDuration((currentPosition / 1000).toDouble()),
                                    fontSize = 12.sp,
                                    color = colors.muted
                                )
                                Text(
                                    formatDuration(story.duration),
                                    fontSize = 12.sp,
                                    color = colors.muted
                                )
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
                                                if (!isReady) return@clickable
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
                                        tint = Color(0xFFFFF0D1),
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
                                                    if (selected) colors.accent else Color(0xFF917A73),
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
                                                color = if (selected) Color(0xFFFFF0D1) else colors.heading
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
                                    inactiveTrackColor =  Color(0xFF917A73).copy(alpha = 0.4f),
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
                                            color = Color(0xFFFFF0D1),
                                            border = BorderStroke(
                                                1.5.dp,
                                                colors.accent
                                            )
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


        }
        // ================= TRANSCRIPT =================
        itemsIndexed(timedTranscript) { index, sentence ->

            val isCurrent = index == currentSentenceIndex
            val isSelected = selectedSentence?.sentenceId == sentence.sentenceId

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        selectedSentence = sentence
                    }
                    .background(
                        when {
                            isSelected -> colors.accent.copy(alpha = 0.15f)
                            isCurrent -> colors.accent.copy(alpha = 0.08f)
                            else -> Color.Transparent
                        },
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {

                Text(
                    text = sentence.speaker,
                    fontSize = 12.sp,
                    color = colors.muted
                )

                Text(
                    text = sentence.text,
                    fontSize = if (isCurrent) 18.sp else 15.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) colors.accent else colors.text
                )

                if (isSelected) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accent,
                            contentColor = Color(0xFFFFF0D1)
                        ),
                        onClick = {
                            showEditDialog = true
                        }
                    ) {
                        Text("Edit")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
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
    if (showEditDialog && selectedSentence != null) {
        AlertDialog(
            containerColor = colors.card,
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    "Edit Sentence Audio",
                    color = colors.heading
                )
            },
            text = {
                Column {

                    Text(
                        "Emotion",
                        color = colors.text
                    )


                    SimpleDropdown(
                        label = "Emotion",
                        selectedItem = selectedEmotion.uppercase(),
                        options = emotions,
//                        options = listOf(
//                            "HAPPINESS",
//                            "SADNESS",
//                            "FEAR",
//                            "ANGER",
//                            "SURPRISE",
//                            "WHISPER",
//                            "NARRATION"
//                        ),
                        onItemSelected = {
                            selectedEmotion = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Intensity",
                        color = colors.text
                    )

                    SimpleDropdown(
                        label = "Intensity",
                        selectedItem = selectedIntensity.uppercase(),
                        options = listOf(
                            "LOW",
                            "MEDIUM",
                            "HIGH"
                        ),
                        onItemSelected = {
                            selectedIntensity = it
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color(0xFFFFF0D1)
                    ),
                    onClick = {
                        val sentence = selectedSentence ?: return@Button
                        Log.d(
                            "EDIT_REQUEST",
                            "storyId=${story.storyId}, sentenceId=${sentence.sentenceId}, emotion=$selectedEmotion, intensity=$selectedIntensity"
                        )
                        scope.launch {
                            try {
                                val request = RequestStoryUpdateDTO(
                                    emotion = selectedEmotion.uppercase(),
                                    intensity = selectedIntensity.uppercase()
                                )

                                Log.d("EDIT_REQUEST", Gson().toJson(request))
                                val updatedStory =
                                    RetrofitClient.createApi(context).editSentence(
                                        storyId = story.storyId,
                                        sentenceId = sentence.sentenceId,
                                        request=request
                                    )
                                mediaPlayer?.release()

                                mediaPlayer = MediaPlayer().apply {
                                    setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                                    setDataSource(updatedStory.finalAudioPath)
                                    prepareAsync()
                                    setOnPreparedListener {
                                        isReady = true
                                    }
                                }

                                showEditDialog = false

                                Toast.makeText(
                                    context,
                                    "Sentence updated",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    e.message ?: "Edit failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF917A73),
                        contentColor = Color(0xFFFFF0D1)
                    ),
                    onClick = {
                        showEditDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun formatDuration(seconds: Double): String {
    val mins = (seconds / 60).toInt()
    val secs = (seconds % 60).toInt()
    return "%d:%02d".format(mins, secs)
}
//val fakeStory = StoryResponseDTO(
//    storyId = "1",
//    creatorId = "1",
//    voiceActors = emptyMap(),
//    title = "The Lost Kingdom",
//    description = "Adventure story preview",
//    tags = emptyList(),
//    genre = "FANTASY",
//    duration = 245.5,
//    isPrivate = false,
//    hasSfx = true,
//    hasBackgroundMusic = true,
//    finalAudioPath = "",
//    jsonPath = "",
//    pdfPath = "",
//    createdAt = java.time.Instant.now().toString(),
//    modifiedAt = java.time.Instant.now().toString(),
//    minimumAge = 10,
//    numberOfViews = 100
//)
//@Preview(showBackground = true)
//@Composable
//fun StoryDetailPreview() {
//    StoryAliveTheme {
//        Column {
//            StoryAliveTopBar(selectedPage = "Published")
//            StoryDetailScreen(
//                story = fakeStory,
//                isLightTheme = true
//            )
//        }
//    }
//}