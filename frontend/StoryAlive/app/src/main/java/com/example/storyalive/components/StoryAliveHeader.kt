package com.example.storyalive.components

import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.storyalive.FavoriteStoriesActivity
import com.example.storyalive.HistoryActivity
import com.example.storyalive.ProfileActivity
import com.example.storyalive.PublishedActivity
import com.example.storyalive.SettingsActivity
import com.example.storyalive.UploadActivity
import com.example.storyalive.privateStoriesActivity
import com.example.storyalive.ui.theme.StoryAliveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryAliveTopBar(selectedPage: String) {

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        title = { Text("StoryAlive") },
        actions = {

            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                MenuItem("Upload", selectedPage) {
                    context.startActivity(Intent(context, UploadActivity::class.java))
                }

                MenuItem("Published", selectedPage) {
                    context.startActivity(Intent(context, PublishedActivity::class.java))
                }

                MenuItem("Private", selectedPage) {
                    context.startActivity(Intent(context, privateStoriesActivity::class.java))
                }

                MenuItem("Favorites", selectedPage) {
                    context.startActivity(Intent(context, FavoriteStoriesActivity::class.java))
                }

                MenuItem("History", selectedPage) {
                    context.startActivity(Intent(context, HistoryActivity::class.java))
                }

                MenuItem("Profile", selectedPage) {
                    context.startActivity(Intent(context, ProfileActivity::class.java))
                }

                MenuItem("Settings", selectedPage) {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            }
        }
    )
}

@Composable
fun MenuItem(
    title: String,
    selectedPage: String,
    onClick: () -> Unit
) {

    val color =
        if (title == selectedPage)
            Color.Blue
        else
            Color.Black

    DropdownMenuItem(
        text = {
            Text(
                text = title,
                color = color
            )
        },
        onClick = onClick
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StoryAliveNavBarPreview() {
    StoryAliveTheme {
        StoryAliveTopBar(selectedPage = "Upload")
    }
}