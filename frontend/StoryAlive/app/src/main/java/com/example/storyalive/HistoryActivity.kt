//package com.example.storyalive
//
//import android.os.Bundle
//import android.widget.Space
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.rememberLazyGridState
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material.icons.filled.SkipNext
//import androidx.compose.material.icons.filled.SkipPrevious
//import androidx.compose.material.icons.outlined.AccessTime
//import androidx.compose.material.icons.outlined.MusicNote
//import androidx.compose.material.icons.outlined.PlayArrow
//import androidx.compose.material.icons.outlined.PlayCircleOutline
//import androidx.compose.material.icons.outlined.SkipNext
//import androidx.compose.material.icons.outlined.SkipPrevious
//import androidx.compose.material.icons.outlined.VolumeUp
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FilledIconButton
//import androidx.compose.material3.FilterChip
//import androidx.compose.material3.FilterChipDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.IconButtonDefaults
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Slider
//import androidx.compose.material3.SliderDefaults
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.storyalive.ui.theme.StoryAliveTheme
//import com.example.storyalive.ui.theme.ThemeColors
//import com.example.storyalive.ui.theme.themeColors
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.draw.clip
//import com.example.storyalive.components.StoryAliveTopBar
//import com.example.storyalive.model.StoryResponseDTO
//import com.example.storyalive.network.RetrofitClient
//import kotlinx.coroutines.launch
//
//class HistoryActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            // State for the theme
//            var isLightTheme by remember { mutableStateOf(true) }
//
//            StoryAliveTheme(darkTheme = !isLightTheme) {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Box(modifier = Modifier.padding(innerPadding)) {
//                        Column {
//                            StoryAliveTopBar(selectedPage = "History")
//                            // Pass the navigation logic here
//                            HistoryScreen(
//                                isLightTheme = isLightTheme,
//                                onStoryClick = { title, date ->
//                                    // THIS IS THE NAVIGATION LOGIC
//                                    val intent = android.content.Intent(
//                                        this@HistoryActivity,
//                                        StoryActivity::class.java
//                                    ).apply {
//                                        putExtra("STORY_TITLE", title)
//                                        putExtra("STORY_DATE", date)
//                                    }
//                                    startActivity(intent)
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//data class StoryHistoryItem(
//    val title: String,
//    val date: String,
//    val duration: String,
//    val hasBgm: Boolean = false,
//    val hasSfx: Boolean = false
//)
//@Composable
//fun HistoryScreen(isLightTheme: Boolean=true,onStoryClick: (String,String) -> Unit){
//    val colors = themeColors(isLightTheme)
//    val context = androidx.compose.ui.platform.LocalContext.current
//    val api = remember { RetrofitClient.createApi(context) }
//    val scope = rememberCoroutineScope()
//
//    var historyStories by remember { mutableStateOf<List<StoryResponseDTO>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var isLoadingMore by remember { mutableStateOf(false) }
//    var currentPage by remember { mutableStateOf(0) }
//    var totalPages by remember { mutableStateOf(1) }
//
//    val listState = rememberLazyGridState()
//    suspend fun loadHistoryStories(page: Int) {
//        try {
//            isLoadingMore = true
//            val response = api.getHistoryStories(pageNumber = page, pageSize = 10)
//            if (response.isSuccessful) {
//                val content = response.body()?.content ?: emptyList()
//                if (page == 0) historyStories = content
//                else historyStories = historyStories + content
//
//                totalPages = response.body()?.totalPages ?: 1
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            isLoading = false
//            isLoadingMore = false
//        }
//    }
//
//    LaunchedEffect(Unit) { loadHistoryStories(0) }
//    // Infinite scroll
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }
//            .collect { index ->
//                if (!isLoadingMore && !isLoading && index >= historyStories.size - 3 && currentPage + 1 < totalPages) {
//                    currentPage++
//                    scope.launch { loadHistoryStories(currentPage) }
//                }
//            }
//    }
//
//    Column(modifier = Modifier.fillMaxSize().background(colors.background).padding(16.dp)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = "Your Story History",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = colors.heading
//                )
//                Text(
//                    text = "Stories you have listened to",
//                    fontSize = 14.sp,
//                    color = colors.muted
//                )
//            }
//
//            Surface(
//                color = Color.White,
//                shape = RoundedCornerShape(8.dp),
//                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
//                shadowElevation = 2.dp
//            ) {
//                Row(
//                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.AccessTime,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp),
//                        tint = colors.muted
//                    )
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(
//                        text = "${historyStories.size} History",
//                        fontSize = 14.sp,
//                        color = colors.muted
//                    )
//                }
//            }
//        }
//        if (isLoading && historyStories.isEmpty()) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(1),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                state = listState,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(historyStories.size) { index ->
//                    val story = historyStories[index]
//                    HistoryStoryCard(
//                        story = story,
//                        colors = colors,
//                        onClick = { onStoryClick(story.title, story.createdAt?: "Unknown Date")}
//                    )
//                }
//
//                if (isLoadingMore) {
//                    item {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//@Composable
//fun HistoryStoryCard(story: StoryResponseDTO,colors: com.example.storyalive.ui.theme.ThemeColors,onClick:()-> Unit){
//    val durationText = remember(story.duration) {
//        val minutes = story.duration.toInt()
//        val seconds = ((story.duration - minutes) * 60).toInt()
//        "%02d:%02d".format(minutes, seconds)
//    }
//    Card(
//        colors= CardDefaults.cardColors(containerColor = colors.card),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        modifier = Modifier.fillMaxWidth().clickable{onClick() },
//    ) {
//        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = story.title,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = colors.heading
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                //Date and Duration
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        imageVector = Icons.Outlined.AccessTime,
//                        contentDescription = null,
//                        tint = colors.muted,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(text=story.createdAt, color = colors.muted, fontSize = 14.sp)
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Icon(
//                        imageVector = Icons.Outlined.PlayCircleOutline,
//                        contentDescription = null,
//                        tint = colors.muted,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(text = durationText,color=colors.muted, fontSize = 14.sp)
//                }
//                Spacer(modifier = Modifier.height(12.dp))
//                //Tags (BGM/SFX)
//                Row{
//                    if (story.hasBackgroundMusic){
//                        TagBadge(text="Background Music",icon= Icons.Outlined.MusicNote,colors=colors)
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//                    if (story.hasSfx){
//                        TagBadge(text="SFX",icon= Icons.Outlined.VolumeUp,colors=colors)
//                    }
//
//                }
//            }
//            //Play Button
//            FilledIconButton(
//                onClick = onClick,
//                shape = RoundedCornerShape(8.dp),
//                modifier = Modifier.size(48.dp),
//                colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.accent)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.PlayArrow,
//                    contentDescription = "Play",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}
//@Composable
//fun TagBadge(text: String, icon: ImageVector, colors: com.example.storyalive.ui.theme.ThemeColors) {
//    Surface(
//        color = colors.background.copy(alpha = 0.5f),
//        shape = RoundedCornerShape(6.dp),
//        border = androidx.compose.foundation.BorderStroke(1.dp, colors.muted.copy(alpha = 0.2f))
//    ) {
//        Row(
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = icon,
//                contentDescription = null,
//                tint = colors.accent,
//                modifier = Modifier.size(14.dp)
//            )
//            Spacer(modifier = Modifier.width(4.dp))
//            Text(
//                text = text,
//                fontSize = 12.sp,
//                color = colors.text.copy(alpha = 0.8f)
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "Light Mode", showSystemUi = true)
//@Composable
//fun HistoryPreviewLight() {
//    StoryAliveTheme {
//        Column {
//            StoryAliveTopBar(selectedPage = "History")
//            // We pass an empty lambda {} because we don't need actual navigation in the preview
//            HistoryScreen(
//                isLightTheme = true,
//                onStoryClick = { title, date -> /* Do nothing in preview */ }
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "Dark Mode", showSystemUi = true)
//@Composable
//fun HistoryPreviewDark() {
//    StoryAliveTheme {
//        Column {
//            StoryAliveTopBar(selectedPage = "History")
//            HistoryScreen(
//                isLightTheme = false,
//                onStoryClick = { title, date -> /* Do nothing in preview */ }
//            )
//        }
//    }
//}