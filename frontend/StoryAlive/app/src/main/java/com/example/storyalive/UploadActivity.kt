package com.example.storyalive

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.model.StoryRequestDTO
import com.example.storyalive.model.VoiceActorDTO
import com.example.storyalive.model.VoiceActorRequest
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.network.createStoryRequestBody
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors
import com.example.storyalive.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.app.Activity


class UploadActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                StoryAliveTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(
                            modifier = Modifier.padding(innerPadding)
                        ) {
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
    val scope = rememberCoroutineScope()
    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    var storyTitle by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    var minAge by rememberSaveable { mutableStateOf("") }

    var backgroundMusic by remember { mutableStateOf(false) }
    var sfx by remember { mutableStateOf(false) }
    var publish by remember { mutableStateOf(false) }

    var selectedActors by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedTags by rememberSaveable { mutableStateOf(setOf<String>()) }

    var genres by remember { mutableStateOf<List<String>>(emptyList()) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }
    var actors by remember { mutableStateOf<List<VoiceActorRequest>>(emptyList()) }
    var currentPage by remember { mutableStateOf(0) }
    var isLoadingActors by remember { mutableStateOf(false) }
    var hasMoreActors by remember { mutableStateOf(true) }

    val addActorLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("NEW_VOICE_ACTOR")?.let { json ->
                val newActor = Gson().fromJson(json, VoiceActorRequest::class.java)
                actors = (actors + newActor).sortedWith(
                    compareByDescending<VoiceActorRequest> { it.private }.thenBy { it.actorName }
                )
            }
        }
    }


    fun loadActorsPage(page: Int, pageSize: Int = 10) {
       if(!hasMoreActors || isLoadingActors) return
        isLoadingActors=true
        scope.launch {
            try {
                val response = RetrofitClient.createApi(context).getUserAvailableVoiceActors(page, pageSize)
                if(response.isSuccessful){
                    val body =response.body()
                    val newActors = body?.content?:emptyList()
                    actors = (actors + newActors).sortedWith(compareByDescending<VoiceActorRequest> { it.private }.thenBy { it.actorName })
                    hasMoreActors = page < (body?.totalPages ?: 1) - 1
                }
            }catch (e: Exception) {
                Toast.makeText(context, "Failed to load actors", Toast.LENGTH_SHORT).show()
            }finally {
                isLoadingActors=false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadActorsPage(currentPage)
        scope.launch {
            try {
                val enums = RetrofitClient.createApi(context).getEnums()
                tags = enums["tags"] ?: emptyList()
                genres = enums["genre"] ?: emptyList()
                if (genres.isNotEmpty() && genre.isBlank()) {
                    genre = genres.first()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            selectedActors,
            { selectedActors = it },
            {
                val intent = Intent(context, VoiceActorActivity::class.java)
                addActorLauncher.launch(intent)
            },
            loadNextPage = {
                if (!isLoadingActors && hasMoreActors) {
                    currentPage++
                    loadActorsPage(currentPage)
                }
            },
            hasMoreActors = hasMoreActors
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
            onClick = {
                if (pdfUri == null) {
                    Toast.makeText(context, "Please select a PDF first", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isProcessing = true
                scope.launch {
                    try {

                        // ✅ Convert PDF
                        val file = uriToFile(context, pdfUri!!)

                        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())

                        val pdfPart = MultipartBody.Part.createFormData(
                            "file", // ✅ MUST match backend
                            file.name,
                            requestFile
                        )

                        // ✅ Create DTO
                        val voiceMap = selectedActors.mapIndexed { index, actorName ->
                            // Example: assign roles dynamically or keep default roles
                            val role = "actor$index" // Or get a role from your UI
                            role to VoiceActorDTO(first = actorName, second = "Narrator")
                        }.toMap()
                        if (storyTitle.isBlank()) {
                            Toast.makeText(context, "Fill story title", Toast.LENGTH_SHORT).show()
                            return@launch
                        }


                        val request = StoryRequestDTO(
                            title = storyTitle,
                            description = description,
                            voiceActors = voiceMap,
                            genre = genre.uppercase(),
                            isPrivate = !publish,
                            hasSfx = sfx,
                            hasBackgroundMusic = backgroundMusic,
                            tags = selectedTags.toList(),
                            minimumAge = minAge.toIntOrNull() ?: 0
                        )

                        val json = Gson().toJson(request)
                        val storyRequestBody = json.toRequestBody("application/json".toMediaTypeOrNull())



                        try {
                            val story = RetrofitClient.createApi(context).createStory(pdfPart, storyRequestBody)

                            Toast.makeText(context, "Upload Success ✅", Toast.LENGTH_SHORT).show()

                            val gson = Gson()
                            val storyJson = gson.toJson(story)

                            val intent = Intent(context, StoryActivity::class.java).apply {
                                putExtra("STORY_JSON", storyJson)
                            }

                        } catch (e: Exception) {
                            Toast.makeText(context, "Upload failed ❌ ${e.message}", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        isProcessing = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    color = colors.buttonText,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processing...", color = colors.buttonText)
            } else {
                Text("Process Story", color = colors.buttonText)
            }
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
    actors: List<VoiceActorRequest>,
    selectedActors: Set<String>,
    onActorSelected: (Set<String>) -> Unit,
    onAddActorClick: () -> Unit,
    loadNextPage: () -> Unit,
    hasMoreActors: Boolean
) {

    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text("3. Select Voice Actor", color = colors.heading)

            Spacer(modifier = Modifier.height(10.dp))

            if (actors.isEmpty()) {
                Text("No voice actors available", color = colors.text)
            } else {
                // Scrollable list with LazyColumn and automatic load more
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(actors) { index, actor ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newSet = if (selectedActors.contains(actor.actorName))
                                        selectedActors - actor.actorName
                                    else
                                        selectedActors + actor.actorName
                                    onActorSelected(newSet)
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(actor.actorName, color = colors.text)
                                Text(
                                    "${actor.gender} • ${if (actor.adult) "Adult" else "Kid"}",
                                    fontSize = 12.sp,
                                    color = colors.text
                                )
                                if (actor.private) {
                                    Surface(
                                        color = colors.accent,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "PRIVATE",
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            ),
                                            color = colors.buttonText
                                        )
                                    }
                                }
                            }

                            Checkbox(
                                checked = selectedActors.contains(actor.actorName),
                                onCheckedChange = { checked ->
                                    val newSet = if (checked)
                                        selectedActors + actor.actorName
                                    else
                                        selectedActors - actor.actorName
                                    onActorSelected(newSet)
                                }
                            )
                        }
                    }
                }

                // Automatic load more when scrolling near the bottom
                LaunchedEffect(listState) {
                    snapshotFlow {
                        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        lastVisible to actors.size
                    }.collect { (lastVisible, total) ->
                        if (hasMoreActors && lastVisible >= total - 3) {
                            loadNextPage()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            val localContext = LocalContext.current
            Button(
                onClick = {
                    onAddActorClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
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