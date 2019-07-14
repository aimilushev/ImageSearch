package com.milushev.imagesearch.data.source.ws

import androidx.annotation.WorkerThread
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WebServiceExecutor {
    /**
     * Executes GET request and returns the successful result (if any) as a string
     */
    @WorkerThread
    @Throws(IOException::class)
    fun executeGetRequest(url: URL): String? {
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