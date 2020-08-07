package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.AppGson
import com.android.ometriasdk.core.Logger
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.Charset

/**
 * Created by cristiandregan
 * on 06/08/2020.
 */

private val TAG = Repository::class.simpleName

internal object Repository {

    private val UTF8 = Charset.forName("UTF-8")

    fun postEventsValidate(request: Any, callback: ApiCallback<PostEventsValidateResponse>) {
        val call = RetrofitBuilder.ometriaApi.postEventsValidate(request)
        call.enqueue(
            object : Callback<PostEventsValidateResponse> {
                override fun onResponse(
                    call: Call<PostEventsValidateResponse>,
                    response: Response<PostEventsValidateResponse>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body()!!)
                    } else {
                        val apiError = getApiError(response)
                        callback.onError(apiError.detail)
                    }
                }

                override fun onFailure(call: Call<PostEventsValidateResponse>, t: Throwable) {
                    callback.onError(t.message)
                }
            })
    }

    private fun getApiError(response: Response<PostEventsValidateResponse>): ApiError {
        try {
            val responseBody: ResponseBody? = response.errorBody()
            val source = responseBody!!.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer

            var charset: Charset? = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }

            if (responseBody.contentLength() != 0L) {
                val responseJSON = buffer.clone().readString(charset!!)
                return AppGson.instance.fromJson(responseJSON, ApiError::class.java)
            }
        } catch (e: Exception) {
            Logger.e(TAG, e.message, e.cause)
        }

        return ApiError()
    }
}