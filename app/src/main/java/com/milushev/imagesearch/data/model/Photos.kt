package com.milushev.imagesearch.data.model

//TODO: rename photo to photoList
data class Photos(
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val total: Int,
    val photo: List<Photo>
)