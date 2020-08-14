package com.android.ometriasdk.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Created by cristiandregan
 * on 06/08/2020.
 */
class OmetriaApiResponse(
    @SerializedName("_received")
    val received: Any
)