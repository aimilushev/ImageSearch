package com.milushev.imagesearch.data.source

import android.net.Uri
import com.milushev.imagesearch.data.json.JsonMapper
import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Result
import com.milushev.imagesearch.data.source.exception.GenericServerException
import com.milushev.imagesearch.data.source.exception.ResponseParsingException
import com.milushev.imagesearch.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class NetworkPhotosDataSource(
    private val networkUtils: NetworkUtils,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PhotosDataSource {

    override suspend fun search(query: String, page: Int): Result<PhotoSearchResult> = withContext(ioDispatcher) {
        //TODO: think if this is necessary
        if (!networkUtils.hasNetworkConnection()) {
            return@withContext Result.Error(IllegalStateException("No internet connectivity"))
        }

        return@withContext try {
            val responseString = executeGetRequest(getSearchUrl(query, page))
            Result.Success(JsonMapper.mapSearchResponse(responseString))
        } catch (io: IOException) {
            Result.Error(GenericServerException())
        } catch (e: Exception) { // TODO change to parsing exception
            Result.Error(ResponseParsingException(e.message))
        }

    }

    private fun getSearchUrl(query: String, page: Int) = URL(
        Uri.Builder()
            .scheme("https")
            .appendEncodedPath(ApiConstants.BASE_URL)
            .appendQueryParameter("method", "flickr.photos.search")
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", 1.toString())
            .appendQueryParameter("safe_search", 1.toString())
            .appendQueryParameter("api_key", ApiConstants.API_KEY)
            .appendQueryParameter("text", query)
            .appendQueryParameter("page", page.toString())
            .build().toString()
    )

    /**
     * Executes GET request and returns the successful result (if any) as a string
     */
    @Throws(IOException::class)
    private fun executeGetRequest(url: URL): String? {
        var connection: HttpsURLConnection? = null
        return try {
            connection = (url.openConnection() as? HttpsURLConnection)
            connection?.run {
                readTimeout = 3000
                connectTimeout = 3000
                requestMethod = "GET"
                doInput = true
                connect()
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }
                inputStream?.let { stream -> readStream(stream) }
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private fun readStream(stream: InputStream): String? {
        val bufferSize = 1024
        val buffer = CharArray(bufferSize)
        val out = StringBuilder()
        val isr = InputStreamReader(stream, "UTF-8")
        while (true) {
            val rsz = isr.read(buffer, 0, buffer.size)
            if (rsz < 0)
                break
            out.append(buffer, 0, rsz)
        }
        return out.toString()
    }
}