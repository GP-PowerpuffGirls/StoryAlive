import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "stories")
data class Story(
    @Id val storyId: ObjectId, //to be added in service
    val creatorId: ObjectId, //to be added in service
    val voiceActors: Map<ObjectId, String>, //to be added in service after llm
    val title: String,
    val description: String,
    val tags: List<Tags>,
    val genre: Genre,
    val duration: Double,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val finalAudioPath: String,
    val jsonPath: String, //to be added in service after llm
    val pdfPath: String,
    val createdAt: Instant, //to be added in service
    val modifiedAt: Instant, //to be added in service
    val minimumAge: Int,
    val numberOfViews: Int//to be updated when the story is gotten getstory()
)