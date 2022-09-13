package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.Constants.Logger.NETWORK
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.network.model.OmetriaApiError
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import org.json.JSONException
import java.io.*
import java.net.HttpURLConnection
import java.nio.charset.Charset

private const val ERROR_RESPONSE_CODE_START_RANGE = 300
private const val SUCCESS_RESPONSE_CODE_START_RANGE = 200
private const val SUCCESS_RESPONSE_CODE_END_RANGE = 299

internal class Client(private val connectionFactory: ConnectionFactory) {

    private val UTF8 = Charset.forName("UTF-8")

    @Throws(IOException::class)
    fun postEvents(
        ometriaApiRequest: OmetriaApiRequest,
        success: () -> Unit,
        error: (OmetriaApiError) -> Unit
    ) {
        val connection: HttpURLConnection = connectionFactory.postConnection()

        val os: OutputStream = connection.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, UTF8))
        writer.write(ometriaApiRequest.toJson().toString())
        writer.flush()
        writer.close()
        os.close()

        val responseCode: Int = connection.responseCode
        if (responseCode in SUCCESS_RESPONSE_CODE_START_RANGE..SUCCESS_RESPONSE_CODE_END_RANGE) {
            success()
        } else if (responseCode >= ERROR_RESPONSE_CODE_START_RANGE) {
            val body = BufferedReader(InputStreamReader(connection.errorStream)).readText()
            try {
                error(body.toOmetriaApiError())
            } catch (e: JSONException) {
                Logger.e(NETWORK, e.message, e)
            }
        }
    }
}