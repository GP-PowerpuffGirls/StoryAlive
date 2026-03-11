package com.example.storyalive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.storyalive.ui.theme.StoryAliveTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.components.StoryAliveTopBar
import com.example.storyalive.ui.theme.themeColors

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // State lives here so it persists during the session
            var isLightTheme by remember { mutableStateOf(true) }

            StoryAliveTheme(darkTheme = !isLightTheme) {
                // Scaffold correctly handles the system bars (status/nav)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // fillMaxSize() here ensures the Box stays within screen bounds
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Column {
                            StoryAliveTopBar(selectedPage = "Settings")
                            SettingsScreen(
                                isLightTheme = isLightTheme,
                                onThemeChange = { isLightTheme = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(isLightTheme: Boolean = true, onThemeChange: (Boolean) -> Unit) {
    val colors = themeColors(isLightTheme)

    // --- State Management ---
    var pushNotifications by remember { mutableStateOf(false) }
    var communityUpdates by remember { mutableStateOf(true) }
    var autoplay by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }
    var inDiscovery by remember { mutableStateOf(true) }

    var expanded by remember { mutableStateOf(false) }
    val speedOptions = listOf("0.5x", "0.75x", "1x (Normal)", "1.25x", "1.5x", "2x")
    var selectedSpeed by remember { mutableStateOf(speedOptions[2]) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- Header ---
        item {
            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.heading,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // --- 1. Edit Profile ---
        item {
            SettingsCard(colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Edit Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.heading
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileInfoRow("Name:", "Alex Johnson", colors)
                        ProfileInfoRow("Email:", "alex.johnson@example.com", colors)
                        ProfileInfoRow("Age:", "28", colors)
                    }
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("Edit", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        // --- 2. Appearance ---
        item {
            SettingsCard(colors) {
                Text(
                    "Appearance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = colors.muted)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Theme",
                            fontWeight = FontWeight.Bold,
                            color = colors.heading,
                            fontSize = 14.sp
                        )
                        Text("Choose your preferred theme", color = colors.muted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = !isLightTheme,
                        onCheckedChange = { onThemeChange(!it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = colors.accent)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeModeSelector(
                        "Light Mode", isLightTheme, Icons.Outlined.LightMode, colors,
                        Modifier
                            .weight(1f)
                            .clickable { onThemeChange(true) }
                    )
                    ThemeModeSelector(
                        "Dark Mode", !isLightTheme, Icons.Outlined.DarkMode, colors,
                        Modifier
                            .weight(1f)
                            .clickable { onThemeChange(false) }
                    )
                }
            }
        }

        // --- 3. Notifications ---
        item {
            SettingsCard(colors) {
                Text(
                    "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotificationRow(
                    Icons.Outlined.Notifications,
                    "Push Notifications",
                    "Receive notifications about your stories",
                    pushNotifications,
                    { pushNotifications = it },
                    colors
                )
                NotificationRow(
                    Icons.Outlined.Public,
                    "Community Updates",
                    "Get notified about new published stories",
                    communityUpdates,
                    { communityUpdates = it },
                    colors
                )
            }
        }

        // --- 4. Audio Settings ---
        item {
            SettingsCard(colors) {
                Text(
                    "Audio Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotificationRow(
                    Icons.Outlined.VolumeUp,
                    "Auto-play Next Story",
                    "Automatically play the next story in queue",
                    autoplay,
                    { autoplay = it },
                    colors
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Default Playback Speed",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(8.dp))

                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedSpeed,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        speedOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { selectedSpeed = option; expanded = false }
                            )
                        }
                    }
                }
            }
        }

        // --- 5. Privacy & Security ---
        item {
            SettingsCard(colors) {
                Text(
                    "Privacy & Security",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotificationRow(
                    Icons.Outlined.Shield,
                    "Make Profile Public",
                    "Allow others to view your profile and stories",
                    isPublic,
                    { isPublic = it },
                    colors
                )
                NotificationRow(
                    Icons.Outlined.Language,
                    "Show in Discovery",
                    "Let others discover your published stories",
                    inDiscovery,
                    { inDiscovery = it },
                    colors
                )
            }
        }

        // --- 6. About ---
        item {
            SettingsCard(colors) {
                Text(
                    "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Version: 1.0.0", fontSize = 14.sp, color = colors.heading)
                Text("Build: 2026.02.23", fontSize = 14.sp, color = colors.heading)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Text(
                        "Terms of Service",
                        color = colors.accent,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { })
                    Text(" • ", color = colors.muted)
                    Text(
                        "Privacy Policy",
                        color = colors.accent,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { })
                }
            }
        }

        // --- 7. Sign Out Button ---
        item {
            Button(
                onClick = { /* Handle sign out logic here */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = "Sign Out",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
        // Extra bottom space to avoid last card being clipped
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
// --- Reusable Components ---

@Composable
fun NotificationRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colors: com.example.storyalive.ui.theme.ThemeColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colors.heading,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = colors.heading, fontSize = 14.sp)
            Text(subtitle, color = colors.muted, fontSize = 12.sp)
        }
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = colors.accent)
        )
    }
}

@Composable
fun SettingsCard(
    colors: com.example.storyalive.ui.theme.ThemeColors,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    colors: com.example.storyalive.ui.theme.ThemeColors
) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = colors.heading, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, color = colors.heading.copy(alpha = 0.8f), fontSize = 14.sp)
    }
}

@Composable
fun ThemeModeSelector(
    label: String,
    isSelected: Boolean,
    icon: ImageVector,
    colors: com.example.storyalive.ui.theme.ThemeColors,
    modifier: Modifier
) {
    val borderColor = if (isSelected) colors.accent else Color.LightGray.copy(alpha = 0.3f)
    Column(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(
                if (isSelected) colors.accent.copy(alpha = 0.05f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSelected) Color.Transparent else Color.LightGray.copy(alpha = 0.2f))
                .border(
                    1.dp,
                    if (isSelected) colors.accent else Color.Transparent,
                    RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) colors.accent else colors.muted,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = if (isSelected) colors.accent else colors.muted)
    }
}

@Preview(showBackground = true, name = "Light Mode Settings")
@Composable
fun SettingsPreviewLight() {
    StoryAliveTheme(darkTheme = false) {
        Column {
            StoryAliveTopBar(selectedPage = "Settings")
            // We pass a dummy lambda {} because we don't need real state logic in a preview
            SettingsScreen(
                isLightTheme = true,
                onThemeChange = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode Settings")
@Composable
fun SettingsPreviewDark() {
    StoryAliveTheme(darkTheme = true) {
        Column {
            StoryAliveTopBar(selectedPage = "Settings")
            SettingsScreen(
                isLightTheme = false,
                onThemeChange = { }
            )
        }
    }
}