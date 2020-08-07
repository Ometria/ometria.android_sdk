package com.android.ometriasdk.core.network

import com.google.gson.annotations.SerializedName

/**
 * Created by cristiandregan
 * on 07/08/2020.
 */
data class ApiError(
    @SerializedName("detail")
    val detail: String? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("type")
    val type: String? = null
)