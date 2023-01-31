package app.model

import java.util.UUID

abstract class NamedItem(id: UUID? = null, override val name: String? = null): Item(id), Named
