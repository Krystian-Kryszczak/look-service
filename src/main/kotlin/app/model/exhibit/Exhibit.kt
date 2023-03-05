package app.model.exhibit

import app.model.NamedItem
import com.datastax.oss.driver.api.core.uuid.Uuids
import com.datastax.oss.driver.api.mapper.annotations.Transient
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID

abstract class Exhibit(
    id: UUID? = null,
    name: String? = null,
    open val creatorId: UUID? = null,
    open var views: Int = 0,
    open var rating: Int = 0,
    open var private: Boolean = false,
): NamedItem(id, name) {
    @Transient
    var creatorName: String? = null
    @Transient
    var creatorSubscriptions: Int? = null

    @Transient
    @JsonIgnore
    fun getUnixTimestamp(): Long? {
        return Uuids.unixTimestamp(id ?: return null)
    }
}
