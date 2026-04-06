import java.time.Instant


data class Story(
    val title: String = "",
    val description: String = "",
    val createdAt: Instant = java.util.Date().toInstant(),
    val duration: Double = 0.0,
    val finalAudioPath: String = "",

    val creatorId: String = "",
    val voiceActors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val genre: String = "",
    val isPrivate: Boolean = false,
    val hasSfx: Boolean = false,
    val hasBackgroundMusic: Boolean = false,
    val jsonPath: String = "",
    val pdfPath: String = "",
    val modifiedAt: Instant = java.util.Date().toInstant(),
    val minimumAge: Int = 0,
    val numberOfViews: Int = 0
)