package com.android.ometriasdk.core.network

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL

class RedirectServiceTest {

    @Before
    fun setup() {
        RedirectService.connectionFactory = { url ->
            URL(url).openConnection() as? HttpURLConnection
        }
    }

    @Test
    fun `getFinalRedirectUrl returns matched domain immediately and does not follow further redirects`() {
        val startUrl = "https://start.com"
        val matchUrl = "https://target.com/page"
        val finalUrl = "https://final.com/result"
        val targetDomain = "target.com"

        val mockConn1 = mockk<HttpURLConnection>(relaxed = true)
        val mockConn2 = mockk<HttpURLConnection>(relaxed = true)

        every { mockConn1.url } returns URL(startUrl)
        every { mockConn1.responseCode } returns 302
        every { mockConn1.getHeaderField("Location") } returns matchUrl

        every { mockConn2.url } returns URL(matchUrl)
        every { mockConn2.responseCode } returns 302
        every { mockConn2.getHeaderField("Location") } returns finalUrl

        RedirectService.connectionFactory = { url ->
            when (url) {
                startUrl -> mockConn1
                matchUrl -> mockConn2
                else -> null
            }
        }

        val result = RedirectService.getFinalRedirectUrl(startUrl, targetDomain, null)

        assertEquals(matchUrl, result)
    }

    @Test
    fun `getFinalRedirectUrl returns starting URL if it matches domain`() {
        val startUrl = "https://target.com/start"
        val nextUrl = "https://other.com/next"
        val targetDomain = "target.com"

        val mockConn1 = mockk<HttpURLConnection>(relaxed = true)

        every { mockConn1.url } returns URL(startUrl)
        every { mockConn1.responseCode } returns 302
        every { mockConn1.getHeaderField("Location") } returns nextUrl

        RedirectService.connectionFactory = { url ->
            if (url == startUrl) mockConn1 else null
        }

        val result = RedirectService.getFinalRedirectUrl(startUrl, targetDomain, null)

        assertEquals(startUrl, result)
    }

    @Test
    fun `getFinalRedirectUrl follows redirects to the end if no match is found`() {
        val startUrl = "https://start.com"
        val middleUrl = "https://middle.com"
        val finalUrl = "https://final.com/result"
        val targetDomain = "target.com"

        val mockConn1 = mockk<HttpURLConnection>(relaxed = true)
        val mockConn2 = mockk<HttpURLConnection>(relaxed = true)
        val mockConn3 = mockk<HttpURLConnection>(relaxed = true)

        every { mockConn1.url } returns URL(startUrl)
        every { mockConn1.responseCode } returns 302
        every { mockConn1.getHeaderField("Location") } returns middleUrl

        every { mockConn2.url } returns URL(middleUrl)
        every { mockConn2.responseCode } returns 302
        every { mockConn2.getHeaderField("Location") } returns finalUrl

        every { mockConn3.url } returns URL(finalUrl)
        every { mockConn3.responseCode } returns 200

        RedirectService.connectionFactory = { url ->
            when (url) {
                startUrl -> mockConn1
                middleUrl -> mockConn2
                finalUrl -> mockConn3
                else -> null
            }
        }

        val result = RedirectService.getFinalRedirectUrl(startUrl, targetDomain, null)

        assertEquals(finalUrl, result)
    }
}
