package com.milushev.imagesearch.data.source

import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result

/**
 * Repository implementation that retrieves search results only from network. If offline cache should be implemented
 * this class can easily be extended by adding offline data souce.
 */
class DefaultPhotosRepository(private val networkDataSource: PhotosDataSource) : PhotosRepository {

    override suspend fun search(query: String, page: Int): Result<PhotoSearchResult> {
        return networkDataSource.search(query, page)
    }

}