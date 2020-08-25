package com.android.ometriasdk.core.network.model


/**
 * Created by cristiandregan
 * on 07/08/2020.
 */

internal data class OmetriaApiError(
    val detail: String? = null,
    val status: Int? = null,
    val title: String? = null,
    val type: String? = null
)