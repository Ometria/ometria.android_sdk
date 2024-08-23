package com.android.sample.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaBasket
import com.android.ometriasdk.core.event.OmetriaBasketItem
import com.android.sample.data.AppPreferencesUtils
import com.android.sample.data.EventType
import com.android.sample.databinding.FragmentHomeBinding
import com.google.firebase.messaging.FirebaseMessaging

private const val POSITION_KEY = "position_key"
const val TAB_ONE = 0
const val TAB_TWO = 1

class HomeFragment : Fragment() {

    private var screenPosition = TAB_ONE
    private val enterApiTokenDialog = EnterApiTokenDialog()
    private lateinit var binding: FragmentHomeBinding
    private val notificationManagerCompat by lazy { NotificationManagerCompat.from(requireContext()) }
    private val requestNotificationPermissionLauncher = initNotificationPermissionLauncher()

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
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenPosition = arguments?.getInt(POSITION_KEY) ?: TAB_ONE
        val ometriaNotificationString = arguments?.getString(OMETRIA_NOTIFICATION_STRING_EXTRA_KEY)

        setUpViews(ometriaNotificationString)
        setUpListeners()
        initEventsRV()
        requestNotificationPermission()
    }

    private fun setUpViews(ometriaNotificationString: String?) {
        binding.detailsBTN.isVisible = screenPosition == TAB_ONE
        binding.eventsRV.isVisible = screenPosition != TAB_ONE
        binding.detailsTV.isVisible = screenPosition == TAB_ONE
        binding.updateApiTokenBTN.isVisible = screenPosition == TAB_ONE
        binding.emailET.isVisible = screenPosition == TAB_ONE
        binding.loginWithEmailBTN.isVisible = screenPosition == TAB_ONE
        binding.customerIdET.isVisible = screenPosition == TAB_ONE
        binding.loginWithCustomerIdBTN.isVisible = screenPosition == TAB_ONE
        binding.storeIdET.isVisible = screenPosition == TAB_ONE

        binding.titleTV.isVisible =
            screenPosition == TAB_ONE && !ometriaNotificationString.isNullOrEmpty()
        binding.detailsTV.isVisible =
            screenPosition == TAB_ONE && !ometriaNotificationString.isNullOrEmpty()
        binding.detailsTV.text = ometriaNotificationString

        if (AppPreferencesUtils.getApiToken().isNullOrEmpty() && screenPosition == TAB_ONE) {
            showEnterApiTokenDialog()
        }
    }

    private fun setUpListeners() {
        binding.detailsBTN.setOnClickListener {
            startActivity(Intent(requireContext(), DetailsActivity::class.java))
        }
        binding.updateApiTokenBTN.setOnClickListener {
            showEnterApiTokenDialog()
        }
        binding.loginWithEmailBTN.setOnClickListener {
            val storeId = binding.storeIdET.text.toString()
            val email = binding.emailET.text.toString()
            Ometria.instance().trackProfileIdentifiedByEmailEvent(email, storeId)
            Ometria.instance().flush()
        }
        binding.loginWithCustomerIdBTN.setOnClickListener {
            val storeId = binding.storeIdET.text.toString()
            val customerId = binding.customerIdET.text.toString()
            Ometria.instance().trackProfileIdentifiedByCustomerIdEvent(customerId, storeId)
            Ometria.instance().flush()
        }
    }

    private fun initEventsRV() {
        binding.eventsRV.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsRV.adapter = EventsAdapter {
            sendEvent(it)
        }
        binding.eventsRV.addItemDecoration(
            DividerItemDecoration(binding.eventsRV.context, DividerItemDecoration.VERTICAL)
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

            EventType.SIMULATE_PUSH_TOKEN_REFRESHED -> FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                Ometria.instance().onNewToken(task.result)
            }

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
            price = 12.0f,
            variantId = "variant-id-1"
        )
        val myItems = listOf(myItem)

        return OmetriaBasket(
            id = "id-1",
            totalPrice = 12.0f,
            currency = "USD",
            items = myItems,
            link = "www.example.com"
        )
    }

    private fun showEnterApiTokenDialog() {
        enterApiTokenDialog.isCancelable = false
        enterApiTokenDialog.show(childFragmentManager, null)
    }

    /**
     * Mocking a basket object
     */
    private fun initNotificationPermissionLauncher() = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your app
        } else {
            // Permission not granted so we are kindly asking the user to take the action of accessing
            // settings and grant the permission from there
            showPermissionRationale()
        }
    }

    /**
     * Check if Notifications Permission is granted and if not proceed with asking for permission
     */
    private fun requestNotificationPermission() {
        if (hasNotificationPermission()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied.
                showPermissionRationale()
            } else {
                // Launch permission prompt
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManagerCompat.areNotificationsEnabled()
        }

    private fun showPermissionRationale() {
        Toast.makeText(
            requireContext(),
            "Please grant Notification Permission from App Settings",
            Toast.LENGTH_LONG
        ).show()
    }
}
