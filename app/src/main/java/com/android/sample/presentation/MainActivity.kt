package com.android.sample.presentation

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.listener.ProcessAppLinkListener
import com.android.sample.R
import kotlinx.android.synthetic.main.activity_main.*

const val OMETRIA_NOTIFICATION_STRING_EXTRA_KEY = "ometria_notification_string_extra_key"
private const val OFF_SCREEN_PAGE_LIMIT = 2
const val FIRST_FRAGMENT_POS = 0
const val SECOND_FRAGMENT_POS = 1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ometriaNotificationString =
            intent.getStringExtra(OMETRIA_NOTIFICATION_STRING_EXTRA_KEY).orEmpty()

        setUpBottomNavMenu()
        setupViewPager(ometriaNotificationString)
        handleAppLinkFromIntent()
    }

    private fun switchFragment(position: Int) {
        containerVP.currentItem = position
    }

    private fun setUpBottomNavMenu() {
        bottomMenuBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.restaurants -> {
                    switchFragment(FIRST_FRAGMENT_POS)
                }
                R.id.my_orders -> {
                    switchFragment(SECOND_FRAGMENT_POS)
                }
            }
            false
        }
    }

    private fun setupViewPager(ometriaNotificationString: String) {
        val adapter = ViewPagerAdapter(supportFragmentManager, ometriaNotificationString)
        containerVP.adapter = adapter
        containerVP.offscreenPageLimit = OFF_SCREEN_PAGE_LIMIT
        switchFragment(0)
        bottomMenuBnv.menu.getItem(0).isChecked = true

        containerVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bottomMenuBnv.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun handleAppLinkFromIntent() {
        // Here you should check whether the link is one that can already be handled by the app.
        // If the link is identified as one coming from an Ometria campaign, you will be able to get the final URL
        // by calling the processAppLink method.
        // The processing is done async, so you should present a loading screen.
        intent.dataString?.let { url ->
            Ometria.instance().processAppLink(url, object : ProcessAppLinkListener {
                override fun onProcessResult(url: String) {
                    displayRedirectUrlDialog(url)
                }

                override fun onProcessFailed(error: String) {
                    displayRedirectUrlDialog(error)
                }
            })
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
}