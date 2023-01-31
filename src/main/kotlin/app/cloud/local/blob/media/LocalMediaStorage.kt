package app.cloud.local.blob.media

import app.cloud.Cloud
import app.storage.blob.media.MediaStorage
import com.datastax.oss.driver.api.core.uuid.Uuids
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.util.UUID
import kotlin.concurrent.thread

abstract class LocalMediaStorage(
    private var containerName: String,
    private var storageDir: String = System.getenv("LOCAL_CLOUD_BLOB_STORAGE_DIR")!!,
): MediaStorage {
    constructor(mediaType: Cloud.MediaType) : this(mediaType.toString())
    private val storageContainer: File = getStorageContainer()
    private fun getStorageContainer(): File {
        if (!storageDir.endsWith("/")) storageDir+="/"
        if (!storageDir.endsWith("/")) containerName+="/"
        val targetDir = storageDir+containerName
        val dir = File(targetDir)
        if (!dir.mkdirs() && !dir.isDirectory) {
            val msg = "Wrong dir path specified in \"LOCAL_CLOUD_BLOB_STORAGE_DIR\"."
            logger.error(msg)
            throw RuntimeException(msg)
        }
        logger.info("Local cloud blob storage dir: $targetDir")
        return dir
    }

    override fun save(inputStream: InputStream, creatorId: UUID, private: Boolean): Single<UUID> {
        val id = Uuids.timeBased()
        return save(id, inputStream, creatorId, private).andThen(Single.just(id))
    }

    override fun save(id: UUID, inputStream: InputStream, creatorId: UUID, private: Boolean): Completable = Completable.create { emitter ->
        val dir = File(storageContainer, id.toString())
        val file = blobFolderCreate(id, creatorId, private, dir)
        if (file != null) {
            thread(true) {
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.transferTo(output)
                    }
                }
            }
        }
        emitter.onComplete()
    }

    override fun save(id: UUID, fileUpload: StreamingFileUpload, creatorId: UUID, private: Boolean): Single<Boolean> = Single.just(File(storageContainer, id.toString()))
        .flatMap {
            val file = blobFolderCreate(id, creatorId, private, it)
            if (file != null) Single.fromPublisher(fileUpload.transferTo(file)) else Single.just(false)
        }

    private fun blobFolderCreate(id: UUID, creatorId: UUID, private: Boolean, dir: File): File? {
        if (!dir.mkdir()) {
            logger.error("Container with id $id already exists!")
            return null
        }
        val file = File(dir, "data")
        if (!file.createNewFile()) {
            logger.error("Container with id $id already exists!")
            return null
        }
        val metadata = File(dir, "metadata")
        if (!metadata.createNewFile()) {
            logger.error("Container with id $id already exists!")
            return null
        }
        if (!metadata.canWrite() && !metadata.setWritable(true)) {
            logger.error("Cannot write to metadata file in $id container!")
            return null
        }
        metadata.writeText("creator_id: $creatorId\nprivate: $private\n")
        return file
    }

    override fun update(id: UUID, clientId: UUID, private: Boolean): Single<Boolean> = updateBlob(id, clientId, null, private)

    override fun update(id: UUID, clientId: UUID, inputStream: InputStream, private: Boolean?): Single<Boolean> = updateBlob(id, clientId, inputStream, private)

    private fun updateBlob(id: UUID, clientId: UUID, inputStream: InputStream?, private: Boolean?): Single<Boolean> = Single.create {
        val dir = File(storageContainer, id.toString())
        val metadata = File(dir, "metadata")
        if (metadata.isFile) {
            val lines = metadata.readLines().toMutableList()
            val clientIsCreator = clientIsCreator(lines, clientId)
            val changedStatus = changePrivateStatus(lines, private, clientIsCreator, metadata)
            if (inputStream != null) {
                val file = File(dir, "data")
                val transferred = inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.transferTo(output)
                    }
                }
                it.onSuccess(transferred > 0 && (private==null || changedStatus))
            } else {
                it.onSuccess(private==null || changedStatus)
            }
        } else {
            it.onSuccess(false)
        }
    }

    private fun clientIsCreator(lines: MutableList<String>, clientId: UUID): Boolean {
        return lines.filter { line -> line.contains("creatorId") }.map { it.contains(clientId.toString()) }.firstOrNull() == true
    }

    private fun changePrivateStatus(lines: MutableList<String>, private: Boolean?, clientIsCreator: Boolean, metadata: File): Boolean {
        if (private==null) return false
        if (clientIsCreator) { // if client is a blob creator
            val status = lines.firstOrNull { line -> line.startsWith("private") }
            if (status!=null) { // if contains blob status
                val index = lines.indexOf(status)
                val data = status.split(": ")
                if (data.size != 2) {
                    lines[index] = "private: $private\n"
                } else {
                    val value = data[1].toBooleanStrictOrNull()
                    if (value!=null) {
                        if (value!=private) {
                            lines[index] = "private: $private\n"
                        }
                    } else {
                        lines[index] = "private: $private\n"
                    }
                }
                metadata.writeText(lines.joinToString())
                return true
            }
        }
        return false
    }

    override fun downloadById(id: UUID): Maybe<out InputStream> = downloadById(id, null)

    override fun downloadById(id: UUID, clientId: UUID?): Maybe<out InputStream> = Maybe.create {
        val dir = File(storageContainer, id.toString())
        val metadata = File(dir, "metadata")
        if (metadata.isFile) {
            val lines = metadata.readLines()
            val isPrivate = lines.firstOrNull{ line -> line.contains("private") }
            if (isPrivate != null && isPrivate.split(": ")[1].toBooleanStrictOrNull() == true) { // if is private
                val creatorId = lines.firstOrNull{ line -> line.contains("creator_id") }
                if (creatorId != null) {
                    val value = creatorId.split(": ")[1]
                    if (clientId != null && value == clientId.toString()) {
                        val data = File(dir, "data")
                        if (data.isFile) it.onSuccess(data.inputStream())
                    }
                }
                it.onComplete()
            } else { // if is not private return input stream
                val data = File(dir, "data")
                if (data.isFile) it.onSuccess(data.inputStream())
                it.onComplete()
            }
        } else { // return input stream if metadata not exists or is not file
            val data = File(dir, "data")
            if (data.isFile) it.onSuccess(data.inputStream())
            it.onComplete()
        }
    }

    override fun deleteById(id: UUID): Completable = Completable.create {
        File(storageContainer, id.toString()).deleteRecursively()
        it.onComplete()
    }

    override fun deleteByIdIfExists(id: UUID): Single<Boolean> = Single.create {
        it.onSuccess(File(storageContainer, id.toString()).deleteRecursively())
    }

    override fun deleteById(id: UUID, clientId: UUID): Single<Boolean> = Single.create {
        val dir = File(storageContainer, id.toString())
        val metadata = File(dir, "metadata")
        if (metadata.isFile && metadata.readLines().firstOrNull { line -> line.contains("creator_id") }?.contains(clientId.toString()) == true) {
            it.onSuccess(dir.deleteRecursively())
        } else {
            it.onSuccess(false)
        }
    }

    override fun deleteByIdIfExists(id: UUID, clientId: UUID): Single<Boolean> = deleteById(id, clientId)

    companion object {
        val logger: Logger = LoggerFactory.getLogger(LocalMediaStorage::class.java)
    }
}
