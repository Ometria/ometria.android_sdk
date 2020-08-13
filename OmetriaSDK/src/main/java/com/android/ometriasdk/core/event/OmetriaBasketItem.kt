package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 12/08/2020.
 */

open class OmetriaBasketItem(
    val productId: String,
    val sku: String,
    val quantity: Int,
    val price: Float
)