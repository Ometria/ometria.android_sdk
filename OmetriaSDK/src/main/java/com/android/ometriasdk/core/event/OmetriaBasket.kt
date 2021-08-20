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
 * @param items (List[OmetriaBasketItem]) An array containing the item entries in this basket.
 * @param link A deeplink to the web or in-app page for this basket. Can be used in
 * a notification sent to the user, e.g. "Forgot to check out? Here's your basket to continue: <link>".
 * Following that link should take them straight to the basket page.
 */
data class OmetriaBasket(
    val totalPrice: Float,
    val currency: String,
    val items: List<OmetriaBasketItem> = listOf(),
    val link: String? = null
)