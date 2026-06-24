package com.example.storyalive

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors
import com.google.gson.Gson

class PublishedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            var isLightTheme by remember { mutableStateOf(true) }

            StoryAliveTheme(darkTheme = !isLightTheme) {

                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->

                    Column(modifier = Modifier.padding(padding)) {

                        StoryAliveTopBar(selectedPage = "Published")

                        PublishedScreen(isLightTheme)
                    }
                }
            }
        }
    }
}
@Composable
fun PublishedScreen(isLightTheme: Boolean) {
    val colors = themeColors(isLightTheme)
    val context = androidx.compose.ui.platform.LocalContext.current
    val api = remember { RetrofitClient.createApi(context) }

    var stories by remember { mutableStateOf<List<StoryResponseDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var editingStory by remember { mutableStateOf<StoryResponseDTO?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var filterGenre by remember { mutableStateOf("") }
    var filterTags by remember { mutableStateOf(listOf<String>()) }
    var publishedGenres by remember { mutableStateOf(listOf<String>()) }
    var publishedTags by remember { mutableStateOf(listOf<String>()) }

    var currentPage by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(1) }
    val gridState = rememberLazyGridState()

    // Load enums once
    LaunchedEffect(Unit) {
        try {
            val enums = api.getEnums()
            publishedGenres = enums["genres"] ?: emptyList()
            publishedTags = enums["tags"] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun formatDuration(durationInHours: Double): String {
        val totalSeconds = (durationInHours * 3600).toInt() // convert hours to seconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }


    // Function to load stories from backend with search/filter
    suspend fun loadStoriesFromApi(page: Int) {
        try {
            isLoadingMore = true
            val response = api.getStories(
                page = page,
                size = 10 // adjust page size as needed
            )

            if (response.isSuccessful) {
                val apiStories = response.body()?.content ?: emptyList()
//
                if (page == 0) {
                    stories = apiStories
                } else {
                    stories = stories + apiStories
                }

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
    LaunchedEffect(searchQuery, filterGenre, filterTags) {
        currentPage = 0
        isLoading = true
        stories = emptyList()
        loadStoriesFromApi(0)
    }


    // Infinite scroll
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { index ->
                if (!isLoadingMore && !isLoading && index >= stories.size - 3 && currentPage + 1 < totalPages) {
                    currentPage++
                    loadStoriesFromApi(currentPage)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {

        Text("Published Stories", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.heading)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Discover stories from the community", color = colors.muted)
        Spacer(modifier = Modifier.height(20.dp))

        // Search + Filter Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search stories") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showFilterDialog = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading && stories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(stories) { story ->
                    PublishedStoryCard(
                        story = story,
                        colors = colors,
                        onPlay = {
                            val storyJson = Gson().toJson(story)

                            val intent = Intent(context, StoryActivity::class.java).apply {
                                putExtra("STORY_JSON", storyJson)
                            }

                            context.startActivity(intent)
                        }
                    )
                }

                if (isLoadingMore) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // Filter dialog
    if (showFilterDialog) {
        FilterDialog(
            selectedGenre = filterGenre,
            selectedTags = filterTags,
            publishedGenres = publishedGenres,
            publishedTags = publishedTags,
            onApply = { genre, tags ->
                filterGenre = genre
                filterTags = tags
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }

    // Edit dialog
    editingStory?.let {
        EditPublishedStoryDialog(
            story = it,
            publishedGenres = publishedGenres,
            publishedTags = publishedTags,
            onDismiss = { editingStory = null }
        )
    }
}

@Composable
fun PublishedStoryCard(
    colors: ThemeColors,
    story: StoryResponseDTO,
    onPlay: () -> Unit,
) {
    val durationText = remember(story.duration) {
       formatDuration(story.duration)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onPlay() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

                // Title
                Text(
                    story.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )

                Spacer(modifier = Modifier.height(4.dp))

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

                // Info row: Duration & Age
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

            // Private/Public badge overlay
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
                        imageVector = if (story.isPrivate) Icons.Outlined.Lock else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (story.isPrivate) "Private" else "Public",
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
fun PublishTagChip(text: String, bgColor: Color, textColor: Color) {

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50) // 🔥 pill shape
    ) {
        Text(
            text,
            color = textColor,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    selectedGenre: String,
    selectedTags: List<String>,
    publishedGenres: List<String>,
    publishedTags: List<String>,
    onApply: (String, List<String>) -> Unit,
    onDismiss: () -> Unit
) {

    var genre by remember { mutableStateOf(selectedGenre) }
    var tags by remember { mutableStateOf(selectedTags) }

    AlertDialog(

        onDismissRequest = onDismiss,

        confirmButton = {

            Button(onClick = { onApply(genre, tags) }) {

                Text("Apply")
            }
        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },

        title = { Text("Filter Stories") },

        text = {

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Text("Genre", fontWeight = FontWeight.Bold)

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    FilterChip(
                        selected = genre == "",
                        onClick = { genre = "" },
                        label = { Text("All") }
                    )

                    publishedGenres.forEach {

                        FilterChip(
                            selected = genre == it,
                            onClick = { genre = it },
                            label = { Text(it) }
                        )
                    }
                }

                Text("Tags", fontWeight = FontWeight.Bold)

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    publishedTags.forEach { tag ->

                        FilterChip(
                            selected = tags.contains(tag),

                            onClick = {

                                tags =
                                    if (tags.contains(tag)) {
                                        tags - tag
                                    } else {
                                        tags + tag
                                    }
                            },

                            label = { Text(tag) }
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditPublishedStoryDialog(
    story: StoryResponseDTO,
    publishedGenres: List<String>,
    publishedTags: List<String>,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(story.title) }
    var description by remember { mutableStateOf(story.description) }
    var genre by remember { mutableStateOf(story.genre) }
    var age by remember { mutableStateOf(story.minimumAge.toString()) }
    var selTags = remember { mutableStateListOf(*story.tags.toTypedArray()) }
    var isPublic by remember { mutableStateOf(!story.isPrivate) }

    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Story") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )

                // Genre Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = genre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Genre") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        publishedGenres.forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    genre = g
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Minimum Age") }
                )

                Text("Tags", fontWeight = FontWeight.Bold)

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    publishedTags.forEach { tag ->
                        FilterChip(
                            selected = selTags.contains(tag),
                            onClick = {
                                if (selTags.contains(tag)) {
                                    selTags.remove(tag)
                                } else {
                                    selTags.add(tag)
                                }
                            },
                            label = { Text(tag) }
                        )
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                    Text("Public Story")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                story.title = title
                story.description = description
                story.genre = genre
                story.minimumAge  = age.toIntOrNull() ?: story.minimumAge
                story.tags = selTags.toList()
                story.isPrivate = !isPublic
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PublishedPreview() {

    StoryAliveTheme {

        Column {

            StoryAliveTopBar(selectedPage = "Published")

            PublishedScreen(isLightTheme = true)
        }
    }
}

