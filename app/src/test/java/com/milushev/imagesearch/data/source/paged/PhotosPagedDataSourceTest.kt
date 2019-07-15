package com.milushev.imagesearch.data.source.paged

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import androidx.paging.PageKeyedDataSource.LoadCallback
import com.milushev.imagesearch.LiveDataTestUtil
import com.milushev.imagesearch.data.model.*
import com.milushev.imagesearch.data.source.PhotosRepository
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class PhotosPagedDataSourceTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var photosRepository: FakeRepository
    private val networkState = MutableLiveData<NetworkState>()
    private lateinit var dataSource: PhotosPagedDataSource

    private val dummyPhoto = Photo("id", "owner", "secret", "server", 1, "title", 1, 1, 1)
    private val dummyResultMoreThanOnePage = PhotoSearchResult(
        Photos(1, 10, 1, 1, listOf(dummyPhoto)),
        "stat"
    )
    private val dummyResultOnlyOnePage = PhotoSearchResult(
        Photos(1, 1, 1, 1, listOf(dummyPhoto)),
        "stat"
    )

    @ExperimentalCoroutinesApi
    @Before
    fun initDataSource() {
        photosRepository = FakeRepository()
        dataSource = PhotosPagedDataSource(photosRepository, networkState, TestCoroutineScope(), "kitten")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadInitial_searchSuccessMoreThanOnePage() = runBlockingTest {
        //GIVEN
        photosRepository.photosResult = Result.Success(dummyResultMoreThanOnePage)
        val callback: PageKeyedDataSource.LoadInitialCallback<Int, Photo> =
            mock(PageKeyedDataSource.LoadInitialCallback::class.java) as PageKeyedDataSource.LoadInitialCallback<Int, Photo>

        //WHEN
        dataSource.loadInitial(PageKeyedDataSource.LoadInitialParams(1, false), callback)

        //THEN
        verify(callback).onResult(dummyResultMoreThanOnePage.photos!!.photo, null, 2)
        assertTrue(LiveDataTestUtil.getValue(networkState).status == Status.SUCCESS)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadInitial_searchSuccessOnlyOnePage() = runBlockingTest {
        //GIVEN
        photosRepository.photosResult = Result.Success(dummyResultOnlyOnePage)
        val callback: PageKeyedDataSource.LoadInitialCallback<Int, Photo> =
            mock(PageKeyedDataSource.LoadInitialCallback::class.java) as PageKeyedDataSource.LoadInitialCallback<Int, Photo>

        //WHEN
        dataSource.loadInitial(PageKeyedDataSource.LoadInitialParams(1, false), callback)

        //THEN
        verify(callback).onResult(dummyResultOnlyOnePage.photos!!.photo, null, 2)
        assertTrue(LiveDataTestUtil.getValue(networkState).status == Status.SUCCESS)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadInitial_fail() = runBlockingTest {
        //GIVEN
        photosRepository.photosResult = Result.Error(Exception())
        val callback: PageKeyedDataSource.LoadInitialCallback<Int, Photo> =
            mock(PageKeyedDataSource.LoadInitialCallback::class.java) as PageKeyedDataSource.LoadInitialCallback<Int, Photo>

        //WHEN
        dataSource.loadInitial(PageKeyedDataSource.LoadInitialParams(1, false), callback)

        //THEN
        assertTrue(LiveDataTestUtil.getValue(networkState).status == Status.FAILED)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadAfter_searchSuccessMoreThanOnePage() = runBlockingTest {
        //GIVEN
        photosRepository.photosResult = Result.Success(dummyResultMoreThanOnePage)
        val callback: LoadCallback<Int, Photo> = mock(LoadCallback::class.java) as LoadCallback<Int, Photo>

        //WHEN
        dataSource.loadAfter(PageKeyedDataSource.LoadParams(1, 10), callback)

        //THEN
        verify(callback).onResult(dummyResultMoreThanOnePage.photos!!.photo, 2)
        assertTrue(LiveDataTestUtil.getValue(networkState).status == Status.SUCCESS)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadAfter_searchSuccessOnlyOnePage() = runBlockingTest {
        //GIVEN
        photosRepository.photosResult = Result.Success(dummyResultOnlyOnePage)
        val callback: LoadCallback<Int, Photo> = mock(LoadCallback::class.java) as LoadCallback<Int, Photo>

        //WHEN
        dataSource.loadAfter(PageKeyedDataSource.LoadParams(1, 10), callback)

        //THEN
        verify(callback).onResult(dummyResultOnlyOnePage.photos!!.photo, null)
        assertTrue(LiveDataTestUtil.getValue(networkState).status == Status.SUCCESS)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadAfter_fail() = runBlockingTest {
        //GIVEN
        photosRepository.photosResult = Result.Error(Exception())
        val callback: LoadCallback<Int, Photo> = mock(LoadCallback::class.java) as LoadCallback<Int, Photo>

        //WHEN
        dataSource.loadAfter(PageKeyedDataSource.LoadParams(1, 10), callback)

        //THEN
        assertTrue(LiveDataTestUtil.getValue(networkState).status == Status.FAILED)
    }

}

class FakeRepository : PhotosRepository {
    var photosResult: Result<PhotoSearchResult> = Result.Error(Exception())

    override suspend fun search(query: String, page: Int): Result<PhotoSearchResult> {
        return photosResult
    }

}
