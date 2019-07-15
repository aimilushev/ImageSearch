package com.milushev.imagesearch.data.source

import com.milushev.imagesearch.data.json.JsonMapper
import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result
import com.milushev.imagesearch.data.source.exception.GenericServerException
import com.milushev.imagesearch.data.source.exception.NoInternetConnectivityException
import com.milushev.imagesearch.data.source.exception.ResponseParsingException
import com.milushev.imagesearch.data.source.ws.WebServiceExecutor
import com.milushev.imagesearch.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.IOException
import java.net.URL


class NetworkPhotosDataSource(
    private val networkUtils: NetworkUtils,
    private val webServiceExecutor: WebServiceExecutor,
    private val jsonMapper: JsonMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PhotosDataSource {

    override suspend fun search(query: String, page: Int): Result<PhotoSearchResult> = withContext(ioDispatcher) {
        if (!networkUtils.hasNetworkConnection()) {
            return@withContext Result.Error(NoInternetConnectivityException())
        }

        return@withContext try {

            webServiceExecutor.executeGetRequest(getSearchUrl(query, page))?.let { responseString ->
                Result.Success(jsonMapper.mapSearchResponse(responseString))
            } ?: Result.Error(GenericServerException("Empty responseBody"))

        } catch (io: IOException) {
            Result.Error(GenericServerException())
        } catch (je: JSONException) {
            Result.Error(ResponseParsingException(je.message))
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    private fun getSearchUrl(query: String, page: Int) =
        URL("${ApiConstants.BASE_URL}?method=flickr.photos.search&api_key=${ApiConstants.API_KEY}&format=json&nojsoncallback=1&safe_search=1&text=$query&page=$page")
}