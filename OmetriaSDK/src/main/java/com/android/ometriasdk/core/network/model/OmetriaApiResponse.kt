package com.android.ometriasdk.core.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by cristiandregan
 * on 06/08/2020.
 */

internal data class OmetriaApiResponse(
    @Expose
    @SerializedName("_received")
    val received: OmetriaApiRequest
)