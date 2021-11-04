package com.android.sample.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaBasket
import com.android.ometriasdk.core.event.OmetriaBasketItem
import com.android.sample.R
import com.android.sample.SampleApp
import com.android.sample.data.AppPreferencesUtils
import com.android.sample.data.EventType
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

private const val POSITION_KEY = "position_key"
const val TAB_ONE = 0
const val TAB_TWO = 1

class HomeFragment : Fragment() {

    private var screenPosition = TAB_ONE

    companion object {
        fun newInstance(position: Int, ometriaNotificationString: String): HomeFragment {
            val instance = HomeFragment()
            instance.arguments = Bundle().apply {
                putInt(POSITION_KEY, position)
                putString(OMETRIA_NOTIFICATION_STRING_EXTRA_KEY, ometriaNotificationString)
            }
            return instance
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenPosition = arguments?.getInt(POSITION_KEY) ?: TAB_ONE
        val ometriaNotificationString = arguments?.getString(OMETRIA_NOTIFICATION_STRING_EXTRA_KEY)

        setUpViews(ometriaNotificationString)
        setUpListeners()
        initEventsRV()
    }

    private fun setUpViews(ometriaNotificationString: String?) {
        detailsBTN.isVisible = screenPosition == TAB_ONE
        eventsRV.isVisible = screenPosition != TAB_ONE
        detailsTV.isVisible = screenPosition == TAB_ONE
        apiTokenET.isVisible = screenPosition == TAB_ONE
        saveAPIKeyBTN.isVisible = screenPosition == TAB_ONE
        emailET.isVisible = screenPosition == TAB_ONE
        loginWithEmailBTN.isVisible = screenPosition == TAB_ONE
        customerIdET.isVisible = screenPosition == TAB_ONE
        loginWithCustomerIdBTN.isVisible = screenPosition == TAB_ONE

        titleTV.isVisible = screenPosition == TAB_ONE && !ometriaNotificationString.isNullOrEmpty()
        detailsTV.isVisible =
            screenPosition == TAB_ONE && !ometriaNotificationString.isNullOrEmpty()
        detailsTV.text = ometriaNotificationString

        apiTokenET.setText(AppPreferencesUtils.getApiToken() ?: "")
    }

    private fun setUpListeners() {
        detailsBTN.setOnClickListener {
            startActivity(Intent(requireContext(), DetailsActivity::class.java))
        }
        saveAPIKeyBTN.setOnClickListener {
            val apiToken = apiTokenET.text.toString()
            Ometria.initialize(
                SampleApp.instance,
                apiToken,
                R.drawable.ic_notification_nys,
                ContextCompat.getColor(requireContext(), R.color.colorAccent)
            ).loggingEnabled(true)
            AppPreferencesUtils.saveApiToken(apiToken)
        }
        loginWithEmailBTN.setOnClickListener {
            val email = emailET.text.toString()
            Ometria.instance().trackProfileIdentifiedByEmailEvent(email)
            Ometria.instance().flush()
        }
        loginWithCustomerIdBTN.setOnClickListener {
            val customerId = customerIdET.text.toString()
            Ometria.instance().trackProfileIdentifiedByCustomerIdEvent(customerId)
            Ometria.instance().flush()
        }
    }

    private fun initEventsRV() {
        eventsRV.layoutManager = LinearLayoutManager(requireContext())
        eventsRV.adapter = EventsAdapter {
            sendEvent(it)
        }
        eventsRV.addItemDecoration(
            DividerItemDecoration(eventsRV.context, DividerItemDecoration.VERTICAL)
        )
    }

    private fun sendEvent(eventType: EventType) {
        when (eventType) {
            EventType.SCREEN_VIEWED -> Ometria.instance()
                .trackScreenViewedEvent("TestScreenName")
            EventType.PROFILE_IDENTIFIED_BY_EMAIL -> Ometria.instance()
                .trackProfileIdentifiedByEmailEvent("test@gmail.com")
            EventType.PROFILE_IDENTIFIED_BY_CUSTOMER_ID -> Ometria.instance()
                .trackProfileIdentifiedByCustomerIdEvent("test_customer_id")
            EventType.PROFILE_DEIDENTIFIED -> Ometria.instance()
                .trackProfileDeidentifiedEvent()
            EventType.PRODUCT_VIEWED -> Ometria.instance()
                .trackProductViewedEvent("product_1")
            EventType.PRODUCT_LISTING_VIEWED -> Ometria.instance().trackProductListingViewedEvent(
                "search",
                mapOf("searchQuery" to "some search terms")
            )
            EventType.WISH_LIST_ADDED_TO -> Ometria.instance()
                .trackWishlistAddedToEvent("product_1")
            EventType.WISHLIST_REMOVED_FROM -> Ometria.instance()
                .trackWishlistRemovedFromEvent("product_1")
            EventType.BASKET_VIEWED -> Ometria.instance()
                .trackBasketViewedEvent()
            EventType.BASKET_UPDATED -> Ometria.instance()
                .trackBasketUpdatedEvent(getBasket())
            EventType.CHECKOUT_STARTED -> Ometria.instance()
                .trackCheckoutStartedEvent("orderId_1")
            EventType.ORDER_COMPLETED -> Ometria.instance()
                .trackOrderCompletedEvent("orderId_1", getBasket())
            EventType.HOME_SCREEN_VIEWED -> Ometria.instance()
                .trackHomeScreenViewedEvent()
            EventType.CUSTOM -> Ometria.instance()
                .trackCustomEvent("my_custom_type", mapOf(Pair("param_key", "param_value")))
            EventType.FLUSH -> Ometria.instance().flush()
            EventType.CLEAR -> Ometria.instance().clear()
        }
    }

    /**
     * Mocking a basket object
     */
    private fun getBasket(): OmetriaBasket {
        val myItem = OmetriaBasketItem(
            productId = "product-1",
            sku = "sku-product-1",
            quantity = 1,
            price = 12.0f
        )
        val myItems = listOf(myItem)

        return OmetriaBasket(
            totalPrice = 12.0f,
            currency = "USD",
            items = myItems,
            link = "www.example.com"
        )
    }
}