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
import javax.net.ssl.HttpsURLConnection

/**
 * Created by cristiandregan
 * on 20/08/2020.
 */

internal class Client(private val connectionFactory: ConnectionFactory) {

    private val UTF8 = Charset.forName("UTF-8")

    @Throws(IOException::class)
    fun postEvents(
        ometriaApiRequest: OmetriaApiRequest,
        callback: ApiCallback<OmetriaApiResponse>
    ) {
        val connection: HttpURLConnection = connectionFactory.postConnection()

        val os: OutputStream = connection.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, UTF8))
        writer.write(getGson().toJson(ometriaApiRequest))
        writer.flush()
        writer.close()
        os.close()

        val responseCode: Int = connection.responseCode // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val br = BufferedReader(InputStreamReader(connection.inputStream))
            val sb = StringBuffer("")
            var line: String? = ""

//            while (`is`.readLine().also { line = it } != null) {
//                sb.append(line)
//                break
//            }

            val responseBody = br.readText()

            val ometriaApiResponse = AppGson.instance.fromJson(
                responseBody,
                OmetriaApiResponse::class.java
            )

            callback.onSuccess(ometriaApiResponse)
        } else if (responseCode >= 300) {
            val br = BufferedReader(InputStreamReader(connection.errorStream))
            val sb = StringBuffer("")
            var line: String? = ""

//            while (`is`.readLine().also { line = it } != null) {
//                sb.append(line)
//                break
//            }

            val responseBody = br.readText()

            val ometriaApiError = AppGson.instance.fromJson(
                responseBody,
                OmetriaApiError::class.java
            )

            callback.onError(ometriaApiError)
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