package com.android.sample.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val ometriaNotificationString: String
) :
    FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int = NUMBER_OF_FRAGMENTS

    override fun createFragment(position: Int): Fragment = when (position) {
        FIRST_FRAGMENT_POS -> HomeFragment.newInstance(TAB_ONE, ometriaNotificationString)
        SECOND_FRAGMENT_POS -> HomeFragment.newInstance(TAB_TWO, ometriaNotificationString)
        else -> throw IllegalArgumentException("Unknown fragment for position $position")
    }

    companion object {
        private const val NUMBER_OF_FRAGMENTS = 2
    }
}