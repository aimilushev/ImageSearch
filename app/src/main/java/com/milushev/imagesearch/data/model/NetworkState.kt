package com.milushev.imagesearch.data.model

sealed class NetworkState {
    object Loaded : NetworkState()
    object Loading : NetworkState()
    object EmptyResult : NetworkState()
    data class Error(val msg: String?) : NetworkState()
}
