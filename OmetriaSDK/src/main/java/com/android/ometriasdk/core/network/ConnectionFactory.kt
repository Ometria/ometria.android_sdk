package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.Constants.API.API_ENDPOINT
import com.android.ometriasdk.core.Constants.API.APPLICATION_JSON
import com.android.ometriasdk.core.Constants.API.HEADER_AUTHENTICATION
import com.android.ometriasdk.core.Constants.API.HEADER_CONTENT_TYPE
import com.android.ometriasdk.core.OmetriaConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Created by cristiandregan
 * on 20/08/2020.
 */

private const val POST = "POST"
private const val TIMEOUT_IN_SECONDS = 30L

internal class ConnectionFactory(private val ometriaConfig: OmetriaConfig) {

    private val timeout = TimeUnit.SECONDS.toMillis(TIMEOUT_IN_SECONDS).toInt()

    fun postConnection(): HttpURLConnection {
        val url = API_ENDPOINT
        val requestedURL: URL
        requestedURL = try {
            URL(url)
        } catch (e: MalformedURLException) {
            throw IOException("Attempted to use malformed url: $url", e)
        }
        val connection = requestedURL.openConnection() as HttpURLConnection
        connection.connectTimeout = timeout
        connection.readTimeout = timeout
        connection.requestMethod = POST
        connection.addRequestProperty(HEADER_AUTHENTICATION, ometriaConfig.apiKey)
        connection.setRequestProperty(HEADER_CONTENT_TYPE, APPLICATION_JSON)
        connection.doInput = true
        connection.doOutput = true
        connection.setChunkedStreamingMode(0)
        return connection
    }
}