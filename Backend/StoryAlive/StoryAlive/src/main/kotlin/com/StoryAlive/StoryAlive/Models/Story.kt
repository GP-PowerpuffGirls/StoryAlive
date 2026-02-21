import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "stories")
data class Story(
    @Id val storyId: ObjectId = ObjectId(),
    val creatorId: ObjectId,
    val voiceActorsIds: List<ObjectId> = emptyList(),
    val title: String,
    val description: String,
    val tags: List<Tags> = emptyList(),
    val genre: Genre,
    val duration: Long,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val finalAudioPath: String,
    val jsonPath: String,
    val pdfPath: String,
    val thumbnailPath: String = "",
    val createdAt: java.time.Instant = java.time.Instant.now(),
    val modifiedAt: java.time.Instant = java.time.Instant.now()
)