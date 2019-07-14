package com.milushev.imagesearch.data.source.paged

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.milushev.imagesearch.data.model.NetworkState
import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.data.source.PhotosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class PhotosPagedDataSourceFactory(
    private val repository: PhotosRepository,
    private val searchQuery: String,
    private val networkState: MutableLiveData<NetworkState>,
    private val coroutineScope: CoroutineScope = GlobalScope
) : DataSource.Factory<Int, Photo>() {

    override fun create(): DataSource<Int, Photo> =
        PhotosPagedDataSource(repository, networkState, coroutineScope, searchQuery)

}