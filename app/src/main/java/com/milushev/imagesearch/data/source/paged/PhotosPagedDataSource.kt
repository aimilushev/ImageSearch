package com.milushev.imagesearch.data.source.paged

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.milushev.imagesearch.data.model.*
import com.milushev.imagesearch.data.source.PhotosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PhotosPagedDataSource(
    private val photosRepository: PhotosRepository,
    private val networkState: MutableLiveData<NetworkState>,
    private val coroutineScope: CoroutineScope,
    private var searchKeyword: String
) : PageKeyedDataSource<Int, Photo>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {
        networkState.postValue(NetworkState.Loading)
        coroutineScope.launch {
            when (val result: Result<PhotoSearchResult> = photosRepository.search(searchKeyword)) {
                is Result.Success -> {
                    result.data.photos?.photo?.let { photosList -> onLoadInitialSuccess(photosList, callback) }
                        ?: onError("null photos list, ws stat=${result.data.stat}")
                }
                is Result.Error -> onError(result.exception.message)
            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        networkState.postValue(NetworkState.Loading)
        coroutineScope.launch {
            when (val result: Result<PhotoSearchResult> = photosRepository.search(searchKeyword, params.key)) {
                is Result.Success -> {
                    result.data.photos?.let { photos -> onLoadAfterSuccess(photos, callback) }
                        ?: onError("null photos list, ws stat=${result.data.stat}")
                }
                is Result.Error -> networkState.postValue(NetworkState.Error(result.exception.message))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        // ignored, since we only ever append to our initial load
    }

    private fun onLoadInitialSuccess(photos: List<Photo>, callback: LoadInitialCallback<Int, Photo>) {
        if (photos.isEmpty()) {
            networkState.postValue(NetworkState.EmptyResult)
        } else {
            callback.onResult(photos, null, 2)
            networkState.postValue(NetworkState.Loaded)
        }
    }

    private fun onLoadAfterSuccess(photos: Photos, callback: LoadCallback<Int, Photo>) {
        val pageNum = photos.page
        val totalPages = photos.pages
        val nextKey: Int? = if (pageNum == totalPages) null else pageNum + 1

        callback.onResult(photos.photo, nextKey)

        if (photos.photo.isEmpty()) {
            networkState.postValue(NetworkState.EmptyResult)
        } else {
            networkState.postValue(NetworkState.Loaded)
        }
    }

    private fun onError(message: String?) {
        networkState.postValue(NetworkState.Error(message))
    }

}