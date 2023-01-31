package app.cloud

import java.util.*

interface Cloud {
    val name: String
    enum class MediaType {
        Video, Image, Audio;
        override fun toString() = name.lowercase(Locale.getDefault())
    }
}
