package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.AppGson
import com.android.ometriasdk.core.network.model.OmetriaApiError
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import com.android.ometriasdk.core.network.model.OmetriaApiResponse
import com.google.gson.Gson
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * Created by cristiandregan
 * on 20/08/2020.
 */

private const val ERROR_RESPONSE_CODE_START_RANGE = 300

internal class Client(private val connectionFactory: ConnectionFactory) {

    private val UTF8 = Charset.forName("UTF-8")
    private val successResponseCodeRange = 200..299

    @Throws(IOException::class)
    fun postEvents(
        ometriaApiRequest: OmetriaApiRequest,
        success: (OmetriaApiResponse) -> Unit,
        error: (OmetriaApiError) -> Unit
    ) {
        val connection: HttpURLConnection = connectionFactory.postConnection()

        val os: OutputStream = connection.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, UTF8))
        writer.write(getGson().toJson(ometriaApiRequest))
        writer.flush()
        writer.close()
        os.close()

        val responseCode: Int = connection.responseCode // To Check for 200
        if (responseCode in successResponseCodeRange) {
            val body = BufferedReader(InputStreamReader(connection.inputStream)).readText()

            val ometriaApiResponse = AppGson.instance.fromJson(
                body,
                OmetriaApiResponse::class.java
            )

            success(ometriaApiResponse)
        } else if (responseCode >= ERROR_RESPONSE_CODE_START_RANGE) {
            val body = BufferedReader(InputStreamReader(connection.errorStream)).readText()

            val ometriaApiError = AppGson.instance.fromJson(
                body,
                OmetriaApiError::class.java
            )

            error(ometriaApiError)
        }
    }

    @Throws(IOException::class)
    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }

    private fun getGson(): Gson {
        return Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}