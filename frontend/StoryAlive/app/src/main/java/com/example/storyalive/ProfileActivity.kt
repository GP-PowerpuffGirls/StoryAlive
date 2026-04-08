package com.example.storyalive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.storyalive.model.UserResponse
import com.example.storyalive.model.VoiceActorRequest
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {
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
                            StoryAliveTopBar(selectedPage = "Profile")
                            ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(isLightTheme: Boolean = true) {

    val colors = themeColors(isLightTheme)
    var privateActors by remember { mutableStateOf<List<VoiceActorRequest>>(emptyList()) }
    val context = LocalContext.current
    var user by remember { mutableStateOf<UserResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEdit by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.createApi(context)

            user = api.getUser()

            val response = api.getPrivateVoiceActors(0, 10)

            if (response.isSuccessful) {
                val body = response.body()
                privateActors = body?.content ?: emptyList()
            } else {
                privateActors = emptyList()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    if (showEdit && user != null) {
        EditProfileScreen(
            user = user!!,
            onCancel = { showEdit = false },
            onSave = { updatedUser, currentPassword, newPassword ->
                // Call API
                scope.launch{
                    try {
                        val response = RetrofitClient.createApi(context).editUser(
                            updatedUser.firstName,
                            updatedUser.lastName,
                            updatedUser.email,
                            updatedUser.age,
                            currentPassword,
                            newPassword
                        )
                        if (response.isSuccessful) {
                            user = response.body()
                            showEdit = false
                        } else {
                            // handle error
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(25.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.heading
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "${user?.firstName} ${user?.lastName}",
                        fontWeight = FontWeight.Bold,
                        color = colors.heading,
                        fontSize = 20.sp
                    )
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoRow("Email:", user?.email ?: "", colors)
                        ProfileInfoRow("Age:", user?.age?.toString() ?: "", colors)
                    Text(
                        "Member since ${user?.accountCreationDate?.substring(0, 10)}",
                        color = colors.muted,
                        fontSize = 14.sp
                    )

                }
            }
        }

        // Statistics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Statistics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.heading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        StatItem("Total Stories", user?.totalStoriesCount ?: 0, colors)
                        StatItem("Published", user?.totalPublishedStoriesCount ?: 0, colors)
                        StatItem("Voice Actors", user?.totalVoiceActorsCount ?: 0, colors)
                    }
                }
            }
        }

        // Your Private Voice Actors
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Your Private Voice Actors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.heading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (privateActors.isEmpty()) {
                        Text("No private voice actors yet", color = colors.text)
                    } else {
                        privateActors.forEach { actor ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = colors.background),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text(
                                        actor.actorName,
                                        color = colors.text,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        "${actor.gender} • ${if (actor.adult) "Adult" else "Kid"}",
                                        fontSize = 12.sp,
                                        color = colors.text
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }

        // Account Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Account Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.heading
                    )

                    AccountButton("Edit Profile", colors){showEdit = true}
                }
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: Int, colors: ThemeColors) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            "$value",
            fontWeight = FontWeight.Bold,
            color = colors.heading,
            fontSize = 18.sp
        )

        Text(
            title,
            color = colors.text,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AccountButton(title: String, colors: ThemeColors,onClick: () -> Unit) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.background
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            title,
            color = colors.text,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    Column {
        StoryAliveTopBar(selectedPage = "Profile")
        ProfileScreen()
    }
}