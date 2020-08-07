package com.android.ometriasdk.core

import com.google.gson.Gson

/**
 * Created by cristiandregan
 * on 07/08/2020.
 */

object AppGson {
    val instance: Gson by lazy { Gson() }
}