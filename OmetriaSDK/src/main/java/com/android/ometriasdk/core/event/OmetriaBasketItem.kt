package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 12/08/2020.
 */

/**
 * An object representing one entry of a particular item in a basket. It can have its own price and
 * quantity based on different rules and promotions that are being applied.
 *
 * @param productId A string representing the unique identifier of this product.
 * @param sku A string representing the stock keeping unit, which allows identifying a particular item.
 * @param quantity The number of items that this entry represents.
 * @param price Float value representing the price for one item. The currency is established by the [OmetriaBasket] containing this item
 */
data class OmetriaBasketItem(
    val productId: String,
    val sku: String? = null,
    val quantity: Int,
    val price: Float,
    val variantId: String? = null
)