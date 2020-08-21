package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.OmetriaConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by cristiandregan
 * on 20/08/2020.
 */

private const val POST: String = "POST"
private const val DEFAULT_READ_TIMEOUT_MILLIS: Int = 15 * 1000 // s
private const val DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000 // 15s

internal class ConnectionFactory(private val ometriaConfig: OmetriaConfig) {

    fun postConnection(): HttpURLConnection {
        val url = "https://mobile-events.ometria.com/v1/mobile-events/validate"
        val requestedURL: URL
        requestedURL = try {
            URL(url)
        } catch (e: MalformedURLException) {
            throw IOException("Attempted to use malformed url: $url", e)
        }
        val connection = requestedURL.openConnection() as HttpURLConnection
        connection.connectTimeout = DEFAULT_CONNECT_TIMEOUT_MILLIS
        connection.readTimeout = DEFAULT_READ_TIMEOUT_MILLIS
        connection.requestMethod = POST
        connection.addRequestProperty("X-Ometria-Auth", ometriaConfig.apiKey)
        connection.setRequestProperty("content-type", "application/json")
        connection.doInput = true
        connection.doOutput = true
        connection.setChunkedStreamingMode(0)
        return connection
    }
}