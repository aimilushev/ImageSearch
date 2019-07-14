package com.milushev.imagesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.milushev.imagesearch.data.source.NetworkPhotosDataSource
import com.milushev.imagesearch.utils.NetworkUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
