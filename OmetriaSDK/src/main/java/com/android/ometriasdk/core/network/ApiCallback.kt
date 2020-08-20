package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.network.model.OmetriaApiError

/**
 * Created by cristiandregan
 * on 07/08/2020.
 */

internal interface ApiCallback<T> {
    fun onSuccess(response: T?)

    fun onError(ometriaApiError: OmetriaApiError?)
}