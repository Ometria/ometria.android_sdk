package com.android.sample.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.webkit.URLUtil
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.listener.ProcessAppLinkListener
import com.android.sample.R
import com.android.sample.SampleApp
import com.android.sample.databinding.ActivityMainBinding

const val OMETRIA_NOTIFICATION_STRING_EXTRA_KEY = "ometria_notification_string_extra_key"
const val DEEPLINK_ACTION_URL_EXTRA_KEY = "deeplink_action_url_extra_key"
const val FIRST_FRAGMENT_POS = 0
const val SECOND_FRAGMENT_POS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ometriaNotificationString = intent.getStringExtra(OMETRIA_NOTIFICATION_STRING_EXTRA_KEY).orEmpty()
        val deepLinkActionUrl = intent.getStringExtra(DEEPLINK_ACTION_URL_EXTRA_KEY)

        setUpBottomNavMenu()
        setupViewPager(ometriaNotificationString)
        handleAppLinkFromIntent(deepLinkActionUrl)
    }

    private fun switchFragment(position: Int) {
        binding.containerVP.currentItem = position
    }

    private fun setUpBottomNavMenu() {
        binding.bottomMenuBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> switchFragment(FIRST_FRAGMENT_POS)
                R.id.events -> switchFragment(SECOND_FRAGMENT_POS)
            }
            false
        }
    }

    private fun setupViewPager(ometriaNotificationString: String) {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, ometriaNotificationString)
        binding.containerVP.adapter = adapter
        switchFragment(0)
        binding.bottomMenuBnv.menu.getItem(0).isChecked = true

        binding.containerVP.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) = Unit

                override fun onPageSelected(position: Int) {
                    binding.bottomMenuBnv.menu.getItem(position).isChecked = true
                }
            }
        )
    }

    private fun handleAppLinkFromIntent(deepLinkActionUrl: String?) {
        // Here you should check whether the link is one that can already be handled by the app.
        // If the link is identified as one coming from an Ometria campaign, you will be able to get the final URL
        // by calling the processAppLink method.
        // The processing is done async, so you should present a loading screen.
        val url = deepLinkActionUrl ?: intent.dataString
        url?.let { safeUrl ->
            Ometria.instance().processAppLink(
                url = safeUrl,
                listener = object : ProcessAppLinkListener {
                    override fun onProcessResult(url: String) {
                        openBrowser(url)
                    }

                    override fun onProcessFailed(error: String) {
                        displayRedirectUrlDialog("$error $safeUrl")
                    }
                }
            )
        }
    }

    private fun displayRedirectUrlDialog(message: String) {
        val messageSpannableString = SpannableString(message)
        Linkify.addLinks(messageSpannableString, Linkify.ALL)

        val textView = TextView(this)
        textView.text = messageSpannableString
        textView.movementMethod = LinkMovementMethod.getInstance()

        AlertDialog.Builder(this)
            .setTitle(R.string.redirect_modal_title)
            .setView(textView)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openBrowser(deepLink: String?) {
        deepLink?.let { safeDeepLink ->
            if (URLUtil.isValidUrl(safeDeepLink).not()) return

            Ometria.instance().trackDeepLinkOpenedEvent(safeDeepLink, "Browser")
            Log.d(SampleApp::class.java.simpleName, "Open URL: $safeDeepLink")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse(safeDeepLink)
            startActivity(intent)
        }
    }
}