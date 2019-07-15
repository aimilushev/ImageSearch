package com.milushev.imagesearch.search

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.milushev.imagesearch.R
import com.milushev.imagesearch.data.model.NetworkState
import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.data.source.PhotosRepository
import com.milushev.imagesearch.data.source.paged.PhotosPagedDataSourceFactory
import com.milushev.imagesearch.utils.Event

private const val INITIAL_SEARCH_PHRASE = "nature"

class SearchViewModel(private val photosRepository: PhotosRepository) : ViewModel() {

    var searchQuery = MutableLiveData<String>().apply { value = INITIAL_SEARCH_PHRASE }

    val foundPhotos: LiveData<PagedList<Photo>>
    val progressBarVisible: LiveData<Boolean>
    val errorMessage: LiveData<Event<Int>>

    private val networkState = MutableLiveData<NetworkState>()

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(30)
        .setPageSize(20).build()

    init {

        foundPhotos = Transformations.switchMap(searchQuery) { query ->
            val feedDataFactory =
                PhotosPagedDataSourceFactory(photosRepository, query, networkState, viewModelScope)
            return@switchMap LivePagedListBuilder(feedDataFactory, pagedListConfig).build()
        }

        progressBarVisible = Transformations.map(networkState) { state ->
            return@map when (state) {
                is NetworkState.LOADING -> true
                else -> false
            }
        }

        errorMessage = Transformations.map(networkState) { state ->
            return@map when (state) {
                is NetworkState.ERROR -> Event(R.string.communication_error)
                else -> null
            }
        }
    }


}


