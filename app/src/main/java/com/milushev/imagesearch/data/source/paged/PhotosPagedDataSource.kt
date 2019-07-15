package com.milushev.imagesearch.data.source.paged

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.milushev.imagesearch.data.model.NetworkState
import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result
import com.milushev.imagesearch.data.source.PhotosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PhotosPagedDataSource(
    private val photosRepository: PhotosRepository,
    private val networkState: MutableLiveData<NetworkState>,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private var searchKeyword: String
) : PageKeyedDataSource<Int, Photo>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch {
            when (val result: Result<PhotoSearchResult> = photosRepository.search(searchKeyword)) {

                is Result.Success -> {
                    result.data.photos?.photo?.let { photosResult ->
                        callback.onResult(photosResult, null, 2)
                        networkState.postValue(NetworkState.LOADED)
                    } ?: networkState.postValue(NetworkState.ERROR("empty photos list, stat=${result.data.stat}"))
                }
                is Result.Error -> networkState.postValue(NetworkState.ERROR(result.exception.message))
            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch {
            when (val result: Result<PhotoSearchResult> = photosRepository.search(searchKeyword, params.key)) {

                is Result.Success -> {
                    result.data.photos?.let { photos ->
                        val pageNum = photos.page
                        val totalPages = photos.pages
                        val nextKey: Int? = if (pageNum == totalPages) null else pageNum + 1
                        callback.onResult(result.data.photos.photo, nextKey)
                        networkState.postValue(NetworkState.LOADED)
                    } ?: networkState.postValue(NetworkState.ERROR("empty photos list, stat=${result.data.stat}"))
                }

                is Result.Error -> networkState.postValue(NetworkState.ERROR(result.exception.message))
            }

        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        // ignored, since we only ever append to our initial load
    }

}