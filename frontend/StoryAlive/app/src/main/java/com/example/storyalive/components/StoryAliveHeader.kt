package com.example.storyalive.components

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.storyalive.ProfileActivity
import com.example.storyalive.PublishedActivity
import com.example.storyalive.SettingsActivity
import com.example.storyalive.UploadActivity
import com.example.storyalive.privateStoriesActivity
import com.example.storyalive.ui.theme.StoryAliveTheme

// Theme Colors
private val Cream = Color(0xFFFFF0D1)
private val Brown = Color(0xFF5E372B)
private val Taupe = Color(0xFF917A73)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryAliveTopBar(selectedPage: String) {

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                text = "StoryAlive",
                color = Cream
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Brown,
            titleContentColor = Cream,
            actionIconContentColor = Cream
        ),
        actions = {

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Cream
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Cream
            ) {

                MenuItem("Upload", selectedPage) {
                    expanded = false
                    context.startActivity(Intent(context, UploadActivity::class.java))
                }

                MenuItem("Published", selectedPage) {
                    expanded = false
                    context.startActivity(Intent(context, PublishedActivity::class.java))
                }

                MenuItem("Private", selectedPage) {
                    expanded = false
                    context.startActivity(Intent(context, privateStoriesActivity::class.java))
                }

//                MenuItem("Favorites", selectedPage) {
//                    expanded = false
//                    context.startActivity(Intent(context, FavoriteStoriesActivity::class.java))
//                }
//
//                MenuItem("History", selectedPage) {
//                    expanded = false
//                    context.startActivity(Intent(context, HistoryActivity::class.java))
//                }

                MenuItem("Profile", selectedPage) {
                    expanded = false
                    context.startActivity(Intent(context, ProfileActivity::class.java))
                }

                MenuItem("Settings", selectedPage) {
                    expanded = false
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

    val textColor =
        if (title == selectedPage)
            Brown
        else
            Taupe

    DropdownMenuItem(
        text = {
            Text(
                text = title,
                color = textColor
            )
        },
        colors = MenuDefaults.itemColors(
            textColor = textColor
        ),
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
//package com.example.storyalive.components
//
//import android.content.Intent
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
////import com.example.storyalive.FavoriteStoriesActivity
////import com.example.storyalive.HistoryActivity
//import com.example.storyalive.ProfileActivity
//import com.example.storyalive.PublishedActivity
//import com.example.storyalive.SettingsActivity
//import com.example.storyalive.UploadActivity
//import com.example.storyalive.privateStoriesActivity
//import com.example.storyalive.ui.theme.StoryAliveTheme
//private val Cream = Color(0xFFFFF0D1)
//private val Brown = Color(0xFF5E372B)
//private val Taupe = Color(0xFF917A73)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StoryAliveTopBar(selectedPage: String) {
//
//    var expanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//
//    TopAppBar(
//        title = { Text(
//            text = "StoryAlive",
//            color = Cream
//        ) },
//        actions = {
//
//            IconButton(onClick = { expanded = true }) {
//                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
//            }
//
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//
//                MenuItem("Upload", selectedPage) {
//                    context.startActivity(Intent(context, UploadActivity::class.java))
//                }
//
//                MenuItem("Published", selectedPage) {
//                    context.startActivity(Intent(context, PublishedActivity::class.java))
//                }
//
//                MenuItem("Private", selectedPage) {
//                    context.startActivity(Intent(context, privateStoriesActivity::class.java))
//                }
//
////                MenuItem("Favorites", selectedPage) {
////                    context.startActivity(Intent(context, FavoriteStoriesActivity::class.java))
////                }
////
////                MenuItem("History", selectedPage) {
////                    context.startActivity(Intent(context, HistoryActivity::class.java))
////                }
//
//                MenuItem("Profile", selectedPage) {
//                    context.startActivity(Intent(context, ProfileActivity::class.java))
//                }
//
//                MenuItem("Settings", selectedPage) {
//                    context.startActivity(Intent(context, SettingsActivity::class.java))
//                }
//            }
//        }
//    )
//}
//
//@Composable
//fun MenuItem(
//    title: String,
//    selectedPage: String,
//    onClick: () -> Unit
//) {
//
//    val color =
//        if (title == selectedPage)
//            Brown
//        else
//            Taupe
//
//    DropdownMenuItem(
//        text = {
//            Text(
//                text = title,
//                color = color
//            )
//        },
//        onClick = onClick
//    )
//}
//
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun StoryAliveNavBarPreview() {
//    StoryAliveTheme {
//        StoryAliveTopBar(selectedPage = "Upload")
//    }
//}