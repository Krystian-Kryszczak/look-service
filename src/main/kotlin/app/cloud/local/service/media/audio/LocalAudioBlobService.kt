package app.cloud.local.service.media.audio

import app.service.blob.media.AbstractMediaBlobService
import app.service.blob.media.audio.AudioBlobService
import app.storage.blob.media.audio.AudioStorage
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
class LocalAudioBlobService(@Named("local") audioStorage: AudioStorage): AbstractMediaBlobService(audioStorage), AudioBlobService
