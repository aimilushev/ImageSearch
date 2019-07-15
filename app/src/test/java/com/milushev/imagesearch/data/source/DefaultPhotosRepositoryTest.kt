package com.milushev.imagesearch.data.source

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class DefaultPhotosRepositoryTest {


    @ExperimentalCoroutinesApi
    @Test
    fun search_callsNetworkDataSource() = runBlockingTest {
        //GIVEN

        val mockedDataSource = mock(PhotosDataSource::class.java)
        val repository = DefaultPhotosRepository(mockedDataSource)

        val searchQuery = "test"
        val page = 23

        //WHEN
        repository.search(searchQuery, page)

        //THEN
        verify(mockedDataSource).search(searchQuery, page)
    }
}