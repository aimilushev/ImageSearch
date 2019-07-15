package com.milushev.imagesearch.data.json

import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Photos
import org.json.JSONException
import org.json.JSONObject


object JsonMapper {

    @Throws(JSONException::class)
    fun mapSearchResponse(jsonResponse: String): PhotoSearchResult {
        val rootJson = JSONObject(jsonResponse)
        var photos: Photos? = null

        rootJson.optJSONObject("photos")?.let { photosJson ->
            val photosPhotoJson = photosJson.getJSONArray("photo")

            val photoList = mutableListOf<Photo>()

            for (i in 0 until photosPhotoJson.length()) {
                val photoObject = photosPhotoJson.getJSONObject(i)

                photoList.add(
                    Photo(
                        photoObject.getString("id"),
                        photoObject.getString("owner"),
                        photoObject.getString("secret"),
                        photoObject.getString("server"),
                        photoObject.getInt("farm"),
                        photoObject.getString("title"),
                        photoObject.getInt("ispublic"),
                        photoObject.getInt("isfriend"),
                        photoObject.getInt("isfamily")
                    )
                )
            }

            photos = Photos(
                photosJson.getInt("page"),
                photosJson.getInt("pages"),
                photosJson.getInt("perpage"),
                photosJson.getInt("total"),
                photoList
            )
        }


        return PhotoSearchResult(photos, rootJson.getString("stat"))

    }
}