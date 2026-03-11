package com.example.storyalive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
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
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors

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

data class PublishedStory(
    val id: Int,
    var title: String,
    var description: String,
    var genre: String,
    var minAge: Int,
    var tags: MutableList<String>,
    var isPublic: Boolean,
    val author: String,
    val duration: String
)

val publishedGenres = listOf(
    "Fantasy",
    "Sci-Fi",
    "Adventure",
    "Drama",
    "Comedy",
    "Educational"
)

val publishedTags = listOf(
    "Adventure",
    "Magic",
    "Space",
    "Future",
    "Friendship",
    "Mystery",
    "Inspirational"
)

val publishedStories = mutableStateListOf(

    PublishedStory(
        1,
        "The Lost Kingdom",
        "A magical adventure through an ancient land.",
        "Fantasy",
        10,
        mutableListOf("Adventure", "Magic"),
        true,
        "Alice",
        "15:20"
    )
)

@Composable
fun PublishedScreen(isLightTheme: Boolean) {

    val colors = themeColors(isLightTheme)

    var searchQuery by remember { mutableStateOf("") }
    var editingStory by remember { mutableStateOf<PublishedStory?>(null) }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filterGenre by remember { mutableStateOf("") }
    var filterTags by remember { mutableStateOf(listOf<String>()) }

    val filteredStories = publishedStories.filter { story ->

        val matchesSearch =
            story.title.contains(searchQuery, true) ||
                    story.description.contains(searchQuery, true)

        val matchesGenre =
            filterGenre.isEmpty() || story.genre == filterGenre

        val matchesTags =
            filterTags.isEmpty() || filterTags.any { story.tags.contains(it) }

        story.isPublic && matchesSearch && matchesGenre && matchesTags
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {

        Text(
            "Published Stories",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.heading
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            "Discover stories from the community",
            color = colors.muted
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search stories") },
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { showFilterDialog = true }
            ) {

                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(filteredStories) { story ->

                PublishedStoryCard(
                    story = story,
                    colors = colors,
                    onEdit = { editingStory = story },
                    onDelete = { publishedStories.remove(story) }
                )
            }
        }
    }

    editingStory?.let {

        EditPublishedStoryDialog(
            story = it,
            onDismiss = { editingStory = null }
        )
    }

    if (showFilterDialog) {

        FilterDialog(
            selectedGenre = filterGenre,
            selectedTags = filterTags,
            onApply = { genre, tags ->

                filterGenre = genre
                filterTags = tags
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun PublishedStoryCard(
    story: PublishedStory,
    colors: ThemeColors,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                AsyncImage(
                    model = "https://images.unsplash.com/photo-1544947950-fa07a98d237f",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.Black.copy(.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {

                    Text(
                        "Public",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            story.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.heading
                        )

                        Text(
                            "by ${story.author}",
                            color = colors.muted,
                            fontSize = 13.sp
                        )
                    }

                    Row {

                        Icon(
                            Icons.Default.Edit,
                            null,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onEdit() }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            Icons.Default.Delete,
                            null,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onDelete() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    story.description,
                    color = colors.muted
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row {

                    story.tags.forEach {

                        PublishTagChip(
                            it,
                            Color.LightGray.copy(.3f),
                            colors.muted
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

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

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            story.duration,
                            color = colors.muted,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        "Age ${story.minAge}+",
                        fontSize = 12.sp,
                        color = colors.muted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(Icons.Default.PlayArrow, null)

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Listen")
                }
            }
        }
    }
}

@Composable
fun PublishTagChip(text: String, bgColor: Color, textColor: Color) {

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {

        Text(
            text,
            color = textColor,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    selectedGenre: String,
    selectedTags: List<String>,
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
    story: PublishedStory,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(story.title) }
    var description by remember { mutableStateOf(story.description) }
    var genre by remember { mutableStateOf(story.genre) }
    var age by remember { mutableStateOf(story.minAge.toString()) }
    var selTags by remember { mutableStateOf(story.tags.toMutableList()) }
    var isPublic by remember { mutableStateOf(story.isPublic) }

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
                story.minAge = age.toIntOrNull() ?: story.minAge
                story.tags = selTags
                story.isPublic = isPublic
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

