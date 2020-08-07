package com.android.ometriasdk.core.network

/**
 * Created by cristiandregan
 * on 07/08/2020.
 */

interface ApiCallback<T> {
    fun onSuccess(response: T)

    fun onError(error: String?)
}