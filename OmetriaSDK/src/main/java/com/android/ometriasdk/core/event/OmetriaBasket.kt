package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 12/08/2020.
 */

/**
 * An object that describes the contents of a shopping basket.
 *
 * @param totalPrice A float value representing the pricing.
 * @param currency A string representing the currency in ISO currency format. e.g. "USD", "GBP"
 * @param items: (List[OmetriaBasketItem]) An array containing the item entries in this basket.
 */
data class OmetriaBasket(
    val totalPrice: Float,
    val currency: String,
    val items: List<OmetriaBasketItem> = listOf()
)