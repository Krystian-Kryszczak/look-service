package app.cloud.local.blob.media.audio

import app.cloud.local.blob.media.LocalMediaStorage
import app.storage.blob.media.audio.AudioStorage
import jakarta.inject.Singleton
import app.cloud.Cloud.MediaType.Audio
import io.micronaut.context.annotation.Primary

@Primary
@Singleton
class LocalAudioStorage: LocalMediaStorage(Audio), AudioStorage
