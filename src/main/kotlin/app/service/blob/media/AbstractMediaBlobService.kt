package app.service.blob.media

import app.service.blob.AbstractBlobService
import app.storage.blob.media.MediaStorage

abstract class AbstractMediaBlobService(mediaStorage: MediaStorage): AbstractBlobService(mediaStorage), MediaBlobService
