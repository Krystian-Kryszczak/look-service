package app.model.exhibit.look

import app.model.exhibit.Exhibit
import com.datastax.oss.driver.api.mapper.annotations.*
import java.util.UUID

@Entity
@SchemaHint(targetElement = SchemaHint.TargetElement.TABLE)
data class Look(
    @PartitionKey
    override val id: UUID? = null,
    override var name: String? = null,
    override val creatorId: UUID? = null,
    var description: String? = null,
    var hasImages: Boolean = false,
    override var views: Int = 0,
    override var rating: Int = 0,
    override var private: Boolean = false
): Exhibit(id, name, creatorId, views, rating, private)
