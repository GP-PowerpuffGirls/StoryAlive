package com.example.storyalive

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.themeColors
import com.example.storyalive.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class VoiceActorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding) // <-- apply scaffold padding
                    ) {
                        StoryAliveTopBar(selectedPage = "Upload")
                        VoiceActorScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceActorScreen(
    modifier: Modifier = Modifier,
    isLightTheme: Boolean = true
) {

    var emotion by remember { mutableStateOf("NARRATION") }
    var emotions by remember { mutableStateOf(listOf<String>()) }
    val colors = themeColors(isLightTheme)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.createApi(context)
            val enums = api.getEnums()
            // Assuming the API returns map with key "EMOTION" for emotions
            emotions = enums["emotions"] ?: listOf("Neutral") // fallback
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load emotions: ${e.message}", Toast.LENGTH_LONG).show()
            emotions = listOf("Neutral") // fallback
        }
    }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var isAdult by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }

    var intensity by remember { mutableStateOf(2) }



    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { audioUri = it }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            "Create Voice Actor",
            style = MaterialTheme.typography.headlineMedium
        )

        /* ---------------- Voice Actor Information ---------------- */

        Card(
            colors = CardDefaults.cardColors(containerColor = colors.card),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Voice Actor Information",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Voice Actor Name *") },
                    placeholder = { Text("Enter voice actor name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Gender *")

                SimpleDropdown(
                    label = "Gender",
                    selectedItem = gender,
                    options = listOf("Female", "Male"),
                    onItemSelected = { gender = it }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAdult,
                        onCheckedChange = { isAdult = it }
                    )
                    Text("Adult Voice Actor")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                    Text("Make Public")
                }
            }
        }

        /* ---------------- Audio Sample ---------------- */

        Card(
            colors = CardDefaults.cardColors(containerColor = colors.card),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Audio Sample",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = {

                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)

                        if (!isRecording) {

                            val file = File(context.cacheDir, "voice_actor_audio.3gp")

                            recorder = MediaRecorder().apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                setOutputFile(file.absolutePath)
                                prepare()
                                start()
                            }

                            audioFilePath = file.absolutePath

                        } else {

                            recorder?.apply {
                                stop()
                                release()
                            }

                            recorder = null
                        }

                        isRecording = !isRecording
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (isRecording) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isRecording) "Stop Recording" else "Record Audio")
                }

                Button(
                    onClick = { launcher.launch("audio/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Audio")
                }

                audioUri?.let {
                    Text("Selected file: ${it.lastPathSegment}")
                }
            }
        }

        /* ---------------- Emotion + Intensity ---------------- */

        Card(
            colors = CardDefaults.cardColors(containerColor = colors.card),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text("Emotion")

                SimpleDropdown(
                    label = "Emotion",
                    selectedItem = emotion,
                    options = emotions,
                    onItemSelected = { emotion = it }
                )

                Text("Intensity: $intensity / 3")

                Slider(
                    value = intensity.toFloat(),
                    onValueChange = { intensity = it.toInt() },
                    valueRange = 1f..3f
                )
            }
        }

        /* ---------------- Buttons ---------------- */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = { context.startActivity(Intent(context, UploadActivity::class.java)) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {

                    // ✅ 1. Validate input
                    if (name.isBlank()) {
                        Toast.makeText(context, "Enter name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (audioFilePath == null && audioUri == null) {
                        Toast.makeText(context, "Record or upload audio", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        try {

                            // ✅ 2. Get audio file
                            val file = when {
                                audioFilePath != null -> File(audioFilePath!!)
                                audioUri != null -> uriToFile(context, audioUri!!)
                                else -> return@launch
                            }

                            // ✅ 3. Convert audio to Multipart
                            val audioPart = MultipartBody.Part.createFormData(
                                "files",
                                file.name,
                                file.asRequestBody("audio/*".toMediaTypeOrNull())
                            )

                            // ✅ 4. Create JSON request
                            val requestMap = mapOf(
                                "actorName" to name,
                                "gender" to gender.uppercase(),
                                "isAdult" to isAdult,
                                "isPrivate" to !isPublic,
                                "preferredRole" to "NONE",
                                "audios" to listOf(
                                    mapOf(
                                        "emotion" to emotion.uppercase(),
                                        "intensity" to intensity.toString(),
                                        "filepath" to ""
                                    )
                                )
                            )

                            val json = Gson().toJson(requestMap)

                            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                            // ✅ 5. Call API
                            RetrofitClient.createApi(context)
                                .createVoiceActor(requestBody, listOf(audioPart))

                            // ✅ 6. Success
                            Toast.makeText(context, "Actor created ✅", Toast.LENGTH_SHORT).show()

                            context.startActivity(Intent(context, UploadActivity::class.java))

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error ❌ ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Create Voice Actor")
            }
        }
    }
}

/* ---------------- Dropdown ---------------- */

@Composable
fun SimpleDropdown(
    label: String,
    selectedItem: String,
    options: List<String>,
    onItemSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
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

            options.forEach { option ->

                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onItemSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VoiceActorPreview() {
    StoryAliveTheme {
        Column {
            StoryAliveTopBar(selectedPage = "Upload")
            VoiceActorScreen()
        }
    }
}