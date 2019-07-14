package com.milushev.imagesearch.data.json

import android.util.Log
import com.google.gson.Gson
import com.milushev.imagesearch.data.model.PhotoSearchResult

//TODO: imlpement yourself
object JsonMapper {

    fun mapSearchResponse(jsonResponse: String?): PhotoSearchResult {
        return Gson().fromJson<PhotoSearchResult>(jsonResponse, PhotoSearchResult::class.java)
    }
}