package app.model.exhibit

import java.util.UUID

abstract class ExhibitModel<T: Exhibit>(
    val name: String? = null,
    val private: Boolean = false,
    val mediaCount: Int = 1
) {
    abstract fun convert(creatorId: UUID): T
}
