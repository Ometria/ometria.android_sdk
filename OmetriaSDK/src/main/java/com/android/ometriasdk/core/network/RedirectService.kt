package com.android.ometriasdk.core.network

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

internal object RedirectService {
    private const val REDIRECT_LOCATION_HEADER = "Location"

    internal var connectionFactory: (String) -> HttpURLConnection? = { url ->
        URL(url).openConnection() as? HttpURLConnection
    }

    fun getFinalRedirectUrl(url: String, domain: String?, regex: Regex?): String {
        var currentUrl: String? = url
        var connection: HttpURLConnection?

        while (true) {
            val urlToProcess = currentUrl ?: break
            connection = connectionFactory(urlToProcess)

            if (domain != null && urlBelongsToDomain(connection?.url, domain)) {
                connection?.disconnect()
                return urlToProcess
            }

            if (regex != null && regex.find(urlToProcess) != null) {
                connection?.disconnect()
                return urlToProcess
            }

            if (domain != null || regex != null) {
                connection?.instanceFollowRedirects = false
            }

            try {
                val responseCode = connection?.responseCode ?: throw IOException()

                if (domain == null && regex == null) {
                    val finalUrl = connection.url.toString()
                    connection.disconnect()
                    return finalUrl
                }

                if (responseCode in 300..399) { // redirect response
                    currentUrl = connection.getHeaderField(REDIRECT_LOCATION_HEADER)
                } else {
                    connection.disconnect()
                    return urlToProcess
                }
            } finally {
                connection?.disconnect()
            }
        }

        return currentUrl ?: url
    }

    private fun urlBelongsToDomain(url: URL?, domain: String): Boolean {
        val host = url?.host ?: return false
        return host == domain || host == "www.$domain"
    }
}