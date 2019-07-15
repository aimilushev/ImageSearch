package com.milushev.imagesearch.data.source

import com.milushev.imagesearch.any
import com.milushev.imagesearch.data.json.JsonMapper
import com.milushev.imagesearch.data.model.PhotoSearchResult
import com.milushev.imagesearch.data.model.Photos
import com.milushev.imagesearch.data.model.Result
import com.milushev.imagesearch.data.source.ws.WebServiceExecutor
import com.milushev.imagesearch.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.json.JSONException
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.IOException
import java.net.URL

class NetworkPhotosDatasourceTest {

    private lateinit var dataSource: NetworkPhotosDataSource
    private lateinit var mockedNetworkUtils: NetworkUtils
    private lateinit var mockedWebServiceExecutor: WebServiceExecutor
    private lateinit var mockedJsonMapper: JsonMapper

    @Before
    fun initDataSource() {
        mockedNetworkUtils = mock(NetworkUtils::class.java)
        mockedWebServiceExecutor = mock(WebServiceExecutor::class.java)
        mockedJsonMapper = mock(JsonMapper::class.java)
        dataSource = NetworkPhotosDataSource(
            mockedNetworkUtils,
            mockedWebServiceExecutor,
            mockedJsonMapper,
            Dispatchers.Unconfined
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun search_callsCorrectUrl() = runBlockingTest {
        //GIVEN
        `when`(mockedNetworkUtils.hasNetworkConnection()).thenReturn(true)

        //WHEN
        val searchKeyword = "search"
        val page = 2
        dataSource.search(searchKeyword, page)

        //THEN
        val correctUrl =
            "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=${ApiConstants.API_KEY}&format=json&nojsoncallback=1&safe_search=1&text=$searchKeyword&page=$page"
        verify(mockedWebServiceExecutor).executeGetRequest(URL(correctUrl))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun search_failWhenNoInternet() = runBlockingTest {
        //GIVEN
        `when`(mockedNetworkUtils.hasNetworkConnection()).thenReturn(false)

        //WHEN
        val result = dataSource.search("search", 2)

        //THEN
        assertTrue(result is Result.Error)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun search_failWhenWebServiceFails() = runBlockingTest {
        //GIVEN
        `when`(mockedNetworkUtils.hasNetworkConnection()).thenReturn(true)
        `when`(mockedWebServiceExecutor.executeGetRequest(any())).thenThrow(IOException())

        //WHEN
        val result = dataSource.search("search", 2)

        //THEN
        assertTrue(result is Result.Error)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun search_failWhenJsonParsingFails() = runBlockingTest {
        //GIVEN
        `when`(mockedNetworkUtils.hasNetworkConnection()).thenReturn(true)
        `when`(mockedWebServiceExecutor.executeGetRequest(any())).thenReturn("mocked response")
        `when`(mockedJsonMapper.mapSearchResponse(any())).thenThrow(JSONException(""))

        //WHEN
        val result = dataSource.search("search", 2)

        //THEN
        assertTrue(result is Result.Error)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun search_Successful() = runBlockingTest {
        //GIVEN
        val searchResult = PhotoSearchResult(Photos(1, 1, 1, 1, listOf()), "stat")
        `when`(mockedNetworkUtils.hasNetworkConnection()).thenReturn(true)
        `when`(mockedWebServiceExecutor.executeGetRequest(any())).thenReturn("mocked response")
        `when`(mockedJsonMapper.mapSearchResponse(any())).thenReturn(searchResult)

        //WHEN
        val result = dataSource.search("search", 2)

        //THEN
        assertTrue(result is Result.Success)
        assertTrue(result is Result.Success && result.data == searchResult)
    }
}