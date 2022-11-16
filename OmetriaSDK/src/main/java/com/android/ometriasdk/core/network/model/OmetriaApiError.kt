package com.android.ometriasdk.core.network.model

internal data class OmetriaApiError(
    val detail: String? = null,
    val status: Int? = null,
    val title: String? = null,
    val type: String? = null
)