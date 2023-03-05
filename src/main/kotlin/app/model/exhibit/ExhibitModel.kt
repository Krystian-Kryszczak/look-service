package app.model.exhibit

import io.micronaut.security.authentication.Authentication

abstract class ExhibitModel<T: Exhibit>(
    val name: String? = null,
    val private: Boolean = false
) {
    abstract fun convert(authentication: Authentication): T?
}
