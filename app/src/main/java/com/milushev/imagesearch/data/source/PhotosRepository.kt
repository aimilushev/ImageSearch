package com.milushev.imagesearch.data.source

import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result

interface PhotosRepository {

    suspend fun search(query: String, page: Int = 1): Result<PhotoSearchResult>

}