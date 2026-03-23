package com.example.storyalive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors

class UploadActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                StoryAliveTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column {
                            StoryAliveTopBar(selectedPage = "Upload")
                            UploadScreen()
                        }
                    }
                }
            }
        }
    }
}

data class VoiceActor(
    val name: String,
    val gender: String,
    val ageGroup: String
)

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier,
    isLightTheme: Boolean = true
) {

    val colors = themeColors(isLightTheme)
    val context = LocalContext.current

    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    var storyTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Fantasy") }
    var minAge by remember { mutableStateOf("") }

    var backgroundMusic by remember { mutableStateOf(false) }
    var sfx by remember { mutableStateOf(false) }
    var publish by remember { mutableStateOf(false) }

    var selectedVoiceActor by remember { mutableStateOf("") }

    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    val genres = listOf("Fantasy", "Adventure", "Horror", "Comedy")

    val tags = listOf(
        "Magic",
        "Adventure",
        "Animals",
        "Friendship",
        "Mystery",
        "Dragons"
    )

    val actors = listOf(
        VoiceActor("Sarah Mitchell", "Female", "Adult"),
        VoiceActor("Tommy Lee", "Male", "Kid"),
        VoiceActor("James Cooper", "Male", "Adult")
    )

    val pdfPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            pdfUri = it
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Upload Your Story",
            fontSize = 28.sp,
            color = colors.heading
        )

        UploadPdfCard(colors, pdfUri) {
            pdfPicker.launch("application/pdf")
        }

        StoryInfoCard(
            colors,
            storyTitle,
            { storyTitle = it },
            description,
            { description = it },
            genre,
            { genre = it },
            genres,
            minAge,
            { minAge = it },
            tags,
            selectedTags,
            { tag ->
                selectedTags =
                    if (selectedTags.contains(tag))
                        selectedTags - tag
                    else
                        selectedTags + tag
            }
        )

        VoiceActorCard(
            colors,
            actors,
            selectedVoiceActor,
            { selectedVoiceActor = it },
            {
                context.startActivity(
                    Intent(context, VoiceActorActivity::class.java)
                )
            }
        )

        AudioOptionsCard(
            colors,
            backgroundMusic,
            { backgroundMusic = it },
            sfx,
            { sfx = it },
            publish,
            { publish = it }
        )

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Process Story", color = colors.buttonText)
        }
    }
}

@Composable
fun UploadPdfCard(
    colors: ThemeColors,
    pdfUri: Uri?,
    onUploadClick: () -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                "1. Upload PDF Story",
                fontSize = 18.sp,
                color = colors.heading
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onUploadClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
            ) {
                Text("Click To Upload PDF", color = colors.buttonText)
            }

            if (pdfUri != null) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Selected: ${pdfUri.lastPathSegment}",
                    color = colors.text
                )
            }
        }
    }
}

@Composable
fun StoryInfoCard(
    colors: ThemeColors,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    genre: String,
    onGenreChange: (String) -> Unit,
    genres: List<String>,
    minAge: String,
    onAgeChange: (String) -> Unit,
    tags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text("2. Story Information", color = colors.heading)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Story Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            GenreDropdown(
                selectedGenre = genre,
                genres = genres,
                onGenreSelected = onGenreChange,
                colors = colors
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = minAge,
                onValueChange = onAgeChange,
                label = { Text("Minimum Age") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Tags", color = colors.heading)

            FlowRow {

                tags.forEach { tag ->

                    val selected = selectedTags.contains(tag)

                    Button(
                        onClick = { onTagToggle(tag) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (selected) colors.accent else colors.background
                        ),
                        border = ButtonDefaults.outlinedButtonBorder,
                        modifier = Modifier.padding(4.dp)
                    ) {

                        Text(tag, color = colors.heading)
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceActorCard(
    colors: ThemeColors,
    actors: List<VoiceActor>,
    selectedActor: String,
    onActorSelected: (String) -> Unit,
    onAddActorClick: () -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text("3. Select Voice Actor", color = colors.heading)

            Spacer(modifier = Modifier.height(10.dp))

            actors.forEach { actor ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(actor.name, color = colors.text)

                        Text(
                            "${actor.gender} • ${actor.ageGroup}",
                            fontSize = 12.sp,
                            color = colors.text
                        )
                    }

                    RadioButton(
                        selected = selectedActor == actor.name,
                        onClick = { onActorSelected(actor.name) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onAddActorClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add New Voice Actor", color = colors.buttonText)
            }
        }
    }
}

@Composable
fun AudioOptionsCard(
    colors: ThemeColors,
    backgroundMusic: Boolean,
    onMusicChange: (Boolean) -> Unit,
    sfx: Boolean,
    onSfxChange: (Boolean) -> Unit,
    publish: Boolean,
    onPublishChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text("4. Audio Options", color = colors.heading)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = backgroundMusic,
                    onCheckedChange = onMusicChange
                )
                Text("Background Music", color = colors.text)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = sfx,
                    onCheckedChange = onSfxChange
                )

                Text("Sound Effects (SFX)", color = colors.text)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = publish,
                    onCheckedChange = onPublishChange
                )

                Text("Publish to storyAlive", color = colors.text)
            }
        }
    }
}

@Composable
fun GenreDropdown(
    selectedGenre: String,
    genres: List<String>,
    onGenreSelected: (String) -> Unit,
    colors: ThemeColors
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedGenre,
            onValueChange = {},
            readOnly = true,
            label = { Text("Genre") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "dropdown"
                )
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {

            genres.forEach { genre ->

                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        onGenreSelected(genre)
                        expanded = false
                    }
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun UploadPreview() {
    StoryAliveTheme {
        Column {
            StoryAliveTopBar(selectedPage = "Upload")
            UploadScreen()
        }
    }
}