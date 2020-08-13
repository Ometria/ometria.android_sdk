package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 12/08/2020.
 */

open class OmetriaBasket(
    val currency: String,
    val totalPrice: Float,
    val items: List<OmetriaBasketItem> = listOf()
)