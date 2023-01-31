package app.service.blob

import app.storage.blob.BlobStorage
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.io.InputStream
import java.util.UUID

/**
 * Abstract class provides function for data manipulate in blob storage.
 */
abstract class AbstractBlobService(private val blobStorage: BlobStorage): BlobService {

    /**
     * Save blob data into storage.
     * @param inputStream is a stream of data to save in the storage.
     * @param creatorId if of blob creator.
     * @param private if true blob is available only for owner.
     */
    override fun save(inputStream: InputStream, creatorId: UUID, private: Boolean): Single<UUID> = blobStorage.save(inputStream, creatorId, private)

    /**
     * Save blob data into storage.
     * @param id identifier of blob data to save in the storage.
     * @param inputStream is a stream of data to save in the storage.
     * @param creatorId if of blob creator.
     * @param private if true blob is available only for owner.
     */
    override fun save(id: UUID, inputStream: InputStream, creatorId: UUID, private: Boolean): Completable = blobStorage.save(id, inputStream, creatorId, private)

    /**
     * Save blob data into storage.
     * @param id identifier of blob data to save in the storage.
     * @param fileUpload represents a part of a io.micronaut.http.MediaType.MULTIPART_FORM_DATA request.
     * @param creatorId if of blob creator.
     * @param private if true blob is available only for owner.
     * @return Single with true when blob data was successful saved without errors otherwise false.
     */
    override fun save(id: UUID, fileUpload: StreamingFileUpload, creatorId: UUID, private: Boolean): Single<Boolean> = blobStorage.save(id, fileUpload, creatorId, private)

    /**
     * Update blob data in the storage.
     * @param id identifier of blob data saved in the storage.
     * @param private if true, the privacy status will set to private otherwise to public.
     * @return Single with true when blob data was successful updated without errors otherwise false.
     */
    override fun update(id: UUID, clientId: UUID, private: Boolean): Single<Boolean> = blobStorage.update(id, clientId, private)

    /**
     * Update blob data in the storage.
     * @param id identifier of blob data saved in the storage.
     * @param inputStream is a stream of data to update in the storage.
     * @param private if private is non-null, the privacy status will not be changed.
     * @return Single with true when blob data was successful updated without errors otherwise false.
     */
    override fun update(id: UUID, clientId: UUID, inputStream: InputStream, private: Boolean?): Single<Boolean> = blobStorage.update(id, clientId, inputStream, private)

    /**
     * Find blob data with given id saved in the storage.
     * @param id identifier of blob data saved in the storage.
     * @return Maybe with output stream data if has been found in the storage otherwise empty.
     */
    override fun downloadById(id: UUID): Maybe<out InputStream> = blobStorage.downloadById(id)

    /**
     * Find blob data with given id saved in the storage.
     * @param id identifier of blob data saved in the storage.
     * @param clientId id of the client who wants to download resource.
     * @return Maybe with output stream data if has been found in the storage otherwise empty.
     */
    override fun downloadById(id: UUID, clientId: UUID?): Maybe<out InputStream> = blobStorage.downloadById(id, clientId)

    /**
     * Deleting blob data with given id.
     * @param id identifier of blob data saved in the storage.
     * @return New instance of Completable.
     */
    override fun deleteById(id: UUID): Completable = blobStorage.deleteById(id)

    /**
     * Deleting blob data with given, if it exists.
     * @param id identifier of blob data saved in the storage.
     * @return Single with true if delete succeeds, or false if blob does not exist.
     */
    override fun deleteByIdIfExists(id: UUID): Single<Boolean> = blobStorage.deleteByIdIfExists(id)

    /**
     * Deleting blob data with given id.
     * @param id identifier of blob data saved in the storage.
     * @param clientId id of the client who wants to delete resource.
     * @return Single with true if delete succeeds, or false if client is not the creator of resource.
     */
    override fun deleteById(id: UUID, clientId: UUID): Single<Boolean> = blobStorage.deleteById(id, clientId)

    /**
     * Deleting blob data with given, if it exists.
     * @param id identifier of blob data saved in the storage.
     * @param clientId id of the client who wants to delete resource.
     * @return Single with true if delete succeeds, or false if blob does not exist or client is not the creator of resource.
     */
    override fun deleteByIdIfExists(id: UUID, clientId: UUID): Single<Boolean> = blobStorage.deleteByIdIfExists(id, clientId)
}
