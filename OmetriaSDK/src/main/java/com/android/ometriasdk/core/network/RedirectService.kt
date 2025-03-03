package com.android.ometriasdk.core.network

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

internal object RedirectService {
    private const val REDIRECT_LOCATION_HEADER = "Location"

    fun getFinalRedirectUrl(url: String, domain: String?, regex: Regex?): String {
        var currentUrl: String? = url
        var connection: HttpURLConnection?
        var lastMatchingUrl: String = url

        while (true) {
            currentUrl ?: break
            val urlTemp = URL(currentUrl)
            connection = urlTemp.openConnection() as? HttpURLConnection

            if (domain != null || regex != null) {
                if (domain != null && urlBelongsToDomain(connection?.url, domain)) {
                    lastMatchingUrl = currentUrl
                } else if (regex != null && regex.find(currentUrl) != null) {
                    lastMatchingUrl = currentUrl
                }
                connection?.instanceFollowRedirects = false
            }

            try {
                val responseCode = connection?.responseCode ?: throw IOException()

                if (domain == null && regex == null) {
                    lastMatchingUrl = connection.url.toString()
                    connection.disconnect()
                    return lastMatchingUrl
                }

                if (responseCode in 300..399) { // redirect response
                    currentUrl = connection.getHeaderField(REDIRECT_LOCATION_HEADER)
                } else {
                    if (domain != null && urlBelongsToDomain(connection.url, domain)) {
                        lastMatchingUrl = currentUrl
                    } else if (regex != null && regex.find(currentUrl) != null) {
                        lastMatchingUrl = currentUrl
                    } else if (lastMatchingUrl == url) {
                        lastMatchingUrl = currentUrl
                    }
                    connection.disconnect()
                    break
                }
            } finally {
                connection?.disconnect()
            }
        }

        return lastMatchingUrl
    }

    private fun urlBelongsToDomain(url: URL?, domain: String): Boolean {
        val host = url?.host ?: return false
        return host == domain || host == "www.$domain"
    }
}