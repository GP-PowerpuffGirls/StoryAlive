package com.example.storyalive

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.ui.theme.StoryAliveTheme
import com.example.storyalive.ui.theme.ThemeColors
import com.example.storyalive.ui.theme.themeColors

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                HistoryScreen()
            }
        }
    }
}
data class StoryHistoryItem(
    val title: String,
    val date: String,
    val duration: String,
    val hasBgm: Boolean = false,
    val hasSfx: Boolean = false
)
@Composable
fun HistoryScreen(isLightTheme: Boolean=true){
    val colors= themeColors(isLightTheme)
    val historyList =listOf(
        StoryHistoryItem("The Adventure Begins", "2/20/2026", "5:32", hasBgm = true),
        StoryHistoryItem("Mystery of the Lost City", "2/18/2026", "8:15", hasBgm = true, hasSfx = true),
        StoryHistoryItem("The Enchanted Garden", "2/15/2026", "6:48", hasSfx = true)
    )
    Column(modifier = Modifier.fillMaxSize().background(colors.background).padding(16.dp)) {
        Text(
            text = "Your Story History",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.heading,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(historyList){
                story->StoryCard(story,colors)
            }
        }
    }
}
@Composable
fun StoryCard(story: StoryHistoryItem,colors: com.example.storyalive.ui.theme.ThemeColors){
    Card(
        colors= CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.heading
                )
                Spacer(modifier = Modifier.height(8.dp))
                //Date and Duration
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text=story.date, color = colors.muted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.PlayCircleOutline,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = story.duration,color=colors.muted, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                //Tags (BGM/SFX)
                Row{
                    if (story.hasBgm){
                        TagBadge(text="Background Music",icon= Icons.Outlined.MusicNote,colors=colors)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (story.hasSfx){
                        TagBadge(text="SFX",icon= Icons.Outlined.VolumeUp,colors=colors)
                    }

                }
            }
            //Play Button
            FilledIconButton(
                onClick = {/*Handle Play*/},
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.accent)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
    }
}
@Composable
fun TagBadge(text: String, icon: ImageVector, colors: com.example.storyalive.ui.theme.ThemeColors) {
    Surface(
        color = colors.background.copy(alpha = 0.5f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.muted.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = colors.text.copy(alpha = 0.8f)
            )
        }
    }
}
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun HistoryPreviewLight() {
    StoryAliveTheme {
        HistoryScreen(isLightTheme = true)
    }
}
@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun HistoryPreviewDark() {
    StoryAliveTheme {
        // Assuming your themeColors handle false for dark mode
        HistoryScreen(isLightTheme = false)
    }
}