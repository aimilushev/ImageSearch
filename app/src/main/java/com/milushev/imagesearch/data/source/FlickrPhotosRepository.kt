package com.milushev.imagesearch.data.source

import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result

class FlickrPhotosRepository(private val networkDataSource: NetworkPhotosDataSource) : PhotosRepository {

    override suspend fun search(query: String, page: Int): Result<PhotoSearchResult> {
        return networkDataSource.search(query, page)
    }

}