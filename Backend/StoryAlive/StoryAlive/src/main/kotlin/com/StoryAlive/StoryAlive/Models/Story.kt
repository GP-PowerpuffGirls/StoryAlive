import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "stories")
data class Story(
    @Id val storyId: ObjectId = ObjectId(), //to be added in service
    val creatorId: ObjectId, //to be added in service
    val voiceActors: Map<ObjectId, String> = emptyMap(), //to be added in service after llm
    val title: String,
    val description: String,
    val tags: List<Tags> = emptyList(),
    val genre: Genre,
    val duration: Long,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val finalAudioPath: String,
    val jsonPath: String, //to be added in service after llm
    val pdfPath: String,
    val createdAt: java.time.Instant = java.time.Instant.now(), //to be added in service
    val modifiedAt: java.time.Instant = java.time.Instant.now(), //to be added in service
    val minimumAge: Int,
    val numberOfViews: Int = 0 //to be updated when the story is gotten getstory()
)