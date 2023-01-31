package app.model.being

import app.model.NamedItem
import java.util.UUID

abstract class Being(id: UUID? = null, name: String? = null): NamedItem(id, name)
