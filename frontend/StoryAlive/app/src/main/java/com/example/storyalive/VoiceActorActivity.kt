package com.example.storyalive

import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.model.VoiceActorRequest
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
import android.app.Activity
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.storyalive.model.AudioRequest
import com.example.storyalive.model.Gender

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
fun getAudioDuration(context: Context, uri: Uri?): Int {
    if (uri == null) return 0
    val mediaPlayer = MediaPlayer()
    return try {
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.prepare()
        mediaPlayer.duration / 1000 // seconds
    } catch (e: Exception) {
        0
    } finally {
        mediaPlayer.release()
    }
}

@Composable
fun VoiceActorScreen(
    modifier: Modifier = Modifier,
    isLightTheme: Boolean = true
) {

    val colors = themeColors(isLightTheme)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var isAdult by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }

    var pendingRecording by remember { mutableStateOf(false) }




    fun startRecording(context: android.content.Context) {
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
        isRecording = true
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingRecording) {
            startRecording(context)
        } else if (!granted) {
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
        pendingRecording = false
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { audioUri = it }
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
            fontSize = 26.sp,
            color = colors.heading
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

                Text("Voice Actor Name *",color = colors.text)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Voice Actor Name") },
                    placeholder = { Text("Enter voice actor name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accent,
                        unfocusedBorderColor = colors.muted,
                        focusedLabelColor = colors.accent,
                        cursorColor = colors.accent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Gender *",color = colors.text)

                SimpleDropdown(
                    label = "Gender",
                    selectedItem = gender,
                    options = listOf("Female", "Male"),
                    onItemSelected = { gender = it }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAdult,
                        onCheckedChange = { isAdult = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.accent
                        )
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
                    color = colors.heading
                )

                Button(
                    onClick = {
                        val micPermission = android.Manifest.permission.RECORD_AUDIO
                        if (ContextCompat.checkSelfPermission(context, micPermission) == PackageManager.PERMISSION_GRANTED) {
                            if (!isRecording) startRecording(context) else stopRecording()
                        } else {
                            pendingRecording = !isRecording
                            permissionLauncher.launch(micPermission)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else colors.accent,
                        contentColor = colors.buttonText
                    )
                ) {
                    Text(if (isRecording) "Stop Recording" else "Record Audio")
                }

                Button(
                    onClick = { launcher.launch("audio/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "Recording Instructions",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("• Make sure your voice is clear")
                Text("• Record in a quiet place (no background noise)")
                Text("• Minimum duration: 3 seconds")
                Text("• Maximum duration: 5 seconds")
            }
        }

        /* ---------------- Buttons ---------------- */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = { context.startActivity(Intent(context, UploadActivity::class.java)) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colors.muted)
            ) {
                Text("Cancel",color=colors.buttonText)
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
                            val audioList = mutableListOf<AudioRequest>()

                            audioFilePath?.let {
                                audioList.add(AudioRequest(
                                    emotion = "NARRATION",
                                    intensity = "LOW",
                                    filepath = it
                                ))
                            }

                            audioUri?.let {
                                val file = uriToFile(context, it)
                                audioList.add(AudioRequest(
                                    emotion = "NARRATION",
                                    intensity = "LOW",
                                    filepath = file.absolutePath
                                ))
                            }

                            val newActor = VoiceActorRequest(
                                actorName = name,
                                gender = Gender.valueOf(gender.uppercase()),
                                adult = isAdult,
                                private = !isPublic,
                                audios = audioList,
                                preferredRole = "NONE"
                            )
                            // ✅ 2. Get audio file
//                            val file = when {
//                                audioFilePath != null -> File(audioFilePath!!)
//                                audioUri != null -> uriToFile(context, audioUri!!)
//                                else -> return@launch
//                            }

                            // ✅ 3. Convert audio to Multipart
                            val audioPart = MultipartBody.Part.createFormData(
                                "files",
                                File(audioFilePath ?: uriToFile(context, audioUri!!).absolutePath).name,
                                (File(audioFilePath ?: uriToFile(context, audioUri!!).absolutePath)).asRequestBody("audio/*".toMediaTypeOrNull())
                            )


                            // ✅ 4. Create JSON request
//                            val requestMap = mapOf(
//                                "actorName" to name,
//                                "gender" to Gender.valueOf(gender.uppercase()),
//                                "isAdult" to isAdult,
//                                "isPrivate" to !isPublic,
//                                "preferredRole" to "NONE",
//                                "audios" to listOf(
//                                    mapOf(
//                                        "emotion" to emotion.uppercase(),
//                                        "intensity" to intensity.toString(),
//                                        "filepath" to ""
//                                    )
//                                )
//                            )
                            val duration = when {
                                audioUri != null -> getAudioDuration(context, audioUri)
                                audioFilePath != null -> {
                                    val mp = MediaPlayer()
                                    try {
                                        mp.setDataSource(audioFilePath)
                                        mp.prepare()
                                        mp.duration / 1000
                                    } catch (e: Exception) {
                                        0
                                    } finally {
                                        mp.release()
                                    }
                                }
                                else -> 0
                            }

                            if (duration < 3 || duration > 5) {
                                Toast.makeText(context, "Audio must be between 3 and 5 seconds", Toast.LENGTH_LONG).show()
                                return@launch
                            }


                            val json = Gson().toJson(newActor)

                            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                            // ✅ 5. Call API
                            RetrofitClient.createApi(context)
                                .createVoiceActor(requestBody, listOf(audioPart))

                            // ✅ 6. Success
                            Toast.makeText(context, "Actor created ✅", Toast.LENGTH_SHORT).show()

                            val resultIntent = Intent().apply {
                                putExtra("NEW_VOICE_ACTOR", json)
                            }
                            val activity = context as? Activity
                            activity?.setResult(Activity.RESULT_OK, resultIntent)
                            activity?.finish()


                        } catch (e: Exception) {
                            Toast.makeText(context, "Error ❌ ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
            ) {
                Text("Create Voice Actor",color=colors.buttonText)
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