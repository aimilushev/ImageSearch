package com.milushev.imagesearch.data.model

sealed class NetworkState {
    object LOADED : NetworkState()
    object LOADING : NetworkState()
    data class ERROR(val msg: String?) : NetworkState()
}
