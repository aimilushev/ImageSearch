package com.milushev.imagesearch.data.source.paged

import androidx.paging.PageKeyedDataSource
import com.milushev.imagesearch.data.model.Photo
import androidx.lifecycle.MutableLiveData
import com.milushev.imagesearch.data.model.NetworkState
import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.milushev.imagesearch.data.source.PhotosRepository
import kotlinx.coroutines.CoroutineScope


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
                    callback.onResult(result.data.photos.photo, null, 2)
                    networkState.postValue(NetworkState.LOADED)
                }
                is Result.Error -> networkState.postValue(NetworkState.error(result.exception.message))
            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch {
            when (val result: Result<PhotoSearchResult> = photosRepository.search(searchKeyword, params.key)) {

                is Result.Success -> {
                    val pageNum = result.data.photos.page
                    val totalPages = result.data.photos.pages
                    val nextKey: Int? = if (pageNum == totalPages) null else pageNum + 1
                    callback.onResult(result.data.photos.photo, nextKey)

                    networkState.postValue(NetworkState.LOADED)
                }

                is Result.Error -> networkState.postValue(NetworkState.error(result.exception.message))
            }

        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        // ignored, since we only ever append to our initial load
    }

}