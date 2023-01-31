package app.cloud.local.blob.media.image

import app.cloud.local.blob.media.LocalMediaStorage
import app.storage.blob.media.image.ImageStorage
import jakarta.inject.Singleton
import app.cloud.Cloud.MediaType.Image
import io.micronaut.context.annotation.Primary

@Primary
@Singleton
class LocalImageStorage: LocalMediaStorage(Image), ImageStorage
