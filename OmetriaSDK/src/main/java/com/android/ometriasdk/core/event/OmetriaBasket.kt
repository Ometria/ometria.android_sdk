package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 12/08/2020.
 */

class OmetriaBasket(
    val totalPrice: Float,
    val currency: String,
    val items: List<OmetriaBasketItem> = listOf()
)