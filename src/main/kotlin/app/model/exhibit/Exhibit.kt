package app.model.exhibit

import app.model.NamedItem
import java.util.UUID

abstract class Exhibit(
    id: UUID? = null,
    name: String? = null,
    open val creatorId: UUID? = null,
    open var views: Int = 0,
    open var rating: Int = 0,
    open var private: Boolean = false,
): NamedItem(id, name)
