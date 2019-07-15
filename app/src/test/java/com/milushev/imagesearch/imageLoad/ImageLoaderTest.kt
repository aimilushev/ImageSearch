package com.milushev.imagesearch.imageLoad

import android.graphics.Bitmap
import android.widget.ImageView
import com.milushev.imagesearch.imageLoad.cache.ImageCache
import com.milushev.imagesearch.imageLoad.network.ImageDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ImageLoaderTest {

    private lateinit var fakeImageDownloader: FakeImageDownloader
    private lateinit var mockedImageCache: ImageCache
    private lateinit var imageLoader: ImageLoader

    private val dummyUrl = "some url"

    @Mock
    private lateinit var mockedResultBitmap: Bitmap

    @Mock
    private lateinit var mockedImageView: ImageView

    @Mock
    private lateinit var mockedCoroutineJob: Job

    @Before
    fun initImageLoader() {
        MockitoAnnotations.initMocks(this)

        mockedImageCache = mock(ImageCache::class.java)
        fakeImageDownloader = FakeImageDownloader(mockedResultBitmap)
        imageLoader = ImageLoader(mockedImageCache, fakeImageDownloader)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadImage_fail() = runBlockingTest {
        //GIVEN
        `when`(mockedImageCache.get(dummyUrl)).thenReturn(null)
        fakeImageDownloader.downloadResult = null

        //WHEN
        imageLoader.loadImage(dummyUrl, mockedImageView, TestCoroutineScope())

        //THEN
        verify(mockedImageView).setImageBitmap(null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadImage_successFromCache() = runBlockingTest {
        //GIVEN
        `when`(mockedImageCache.get(dummyUrl)).thenReturn(mockedResultBitmap)
        fakeImageDownloader.downloadResult = null

        //WHEN
        imageLoader.loadImage(dummyUrl, mockedImageView, TestCoroutineScope(), Dispatchers.Unconfined)

        //THEN
        verify(mockedImageView).setImageBitmap(mockedResultBitmap)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadImage_successFromNetworkAndCacheUpdated() = runBlockingTest {
        //GIVEN
        `when`(mockedImageCache.get(dummyUrl)).thenReturn(null)
        `when`(mockedImageView.tag).thenReturn(ImageViewTag(dummyUrl, mockedCoroutineJob))
        fakeImageDownloader.downloadResult = mockedResultBitmap

        //WHEN
        imageLoader.loadImage(dummyUrl, mockedImageView, TestCoroutineScope(), Dispatchers.Unconfined)

        //THEN
        verify(mockedImageCache).put(dummyUrl, mockedResultBitmap)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadImage_fromNetworkImageViewNotReused() = runBlockingTest {
        //GIVEN
        `when`(mockedImageCache.get(dummyUrl)).thenReturn(null)
        `when`(mockedImageView.tag).thenReturn(ImageViewTag(dummyUrl, mockedCoroutineJob))
        fakeImageDownloader.downloadResult = mockedResultBitmap

        //WHEN
        imageLoader.loadImage(dummyUrl, mockedImageView, TestCoroutineScope(), Dispatchers.Unconfined)

        //THEN
        verify(mockedImageView).setImageBitmap(null)
        verify(mockedImageView).setImageBitmap(mockedResultBitmap)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadImage_fromNetworkImageViewReusedAndDownloadActive() = runBlockingTest {
        //GIVEN
        `when`(mockedImageCache.get(dummyUrl)).thenReturn(null)
        `when`(mockedImageView.tag).thenReturn(ImageViewTag("some other url", mockedCoroutineJob))
        `when`(mockedCoroutineJob.isActive).thenReturn(true)
        fakeImageDownloader.downloadResult = mockedResultBitmap

        //WHEN
        imageLoader.loadImage(dummyUrl, mockedImageView, TestCoroutineScope(), Dispatchers.Unconfined)

        //THEN
        verify(mockedImageView).setImageBitmap(null)
        verify(mockedCoroutineJob).cancel()
        verify(mockedImageView, never()).setImageBitmap(mockedResultBitmap)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadImage_fromNetworkImageViewReusedAndDownloadInactive() = runBlockingTest {
        //GIVEN
        `when`(mockedImageCache.get(dummyUrl)).thenReturn(null)
        `when`(mockedImageView.tag).thenReturn(ImageViewTag("some other url", mockedCoroutineJob))
        `when`(mockedCoroutineJob.isActive).thenReturn(false)
        fakeImageDownloader.downloadResult = mockedResultBitmap

        //WHEN
        imageLoader.loadImage(dummyUrl, mockedImageView, TestCoroutineScope(), Dispatchers.Unconfined)

        //THEN
        verify(mockedImageView).setImageBitmap(null)
        verify(mockedCoroutineJob, never()).cancel()
        verify(mockedImageView, never()).setImageBitmap(mockedResultBitmap)
    }

}

class FakeImageDownloader(var downloadResult: Bitmap?) : ImageDownloader {
    override suspend fun downloadImage(url: String) = downloadResult
}