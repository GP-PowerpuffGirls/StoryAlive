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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                StoryAliveTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column {
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

    val profile = remember {
        mapOf(
            "name" to "Mariam Tamer",
            "email" to "Mariam.Tamer@example.com",
            "joinDate" to "2025-12-01",
            "totalStories" to 5,
            "publishedStories" to 1,
            "totalListens" to 234,
            "totalVoiceActors" to 3,
            "favoriteVoiceActors" to listOf("Sarah Mitchell", "James Cooper")
        )
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

        // Profile Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        profile["name"] as String,
                        fontWeight = FontWeight.Bold,
                        color = colors.heading,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        profile["email"] as String,
                        color = colors.muted,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Member since ${profile["joinDate"]}",
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

                        StatItem("Total Stories", profile["totalStories"] as Int, colors)

                        StatItem("Published", profile["publishedStories"] as Int, colors)

                        StatItem("Total Listens", profile["totalListens"] as Int, colors)

                        StatItem("Voice Actors", profile["totalVoiceActors"] as Int, colors)
                    }
                }
            }
        }

        // Favorite Voice Actors
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Favorite Voice Actors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.heading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    (profile["favoriteVoiceActors"] as List<String>).forEach { actor ->

                        Card(
                            colors = CardDefaults.cardColors(containerColor = colors.background),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                actor,
                                color = colors.text,
                                modifier = Modifier.padding(12.dp)
                            )
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

                    AccountButton("Edit Profile", colors)

                    AccountButton("Change Password", colors)
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
fun AccountButton(title: String, colors: ThemeColors) {

    Button(
        onClick = {},
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