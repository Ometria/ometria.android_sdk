package com.android.sample.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

private const val SCREENS_NO = 3

class ViewPagerAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            FIRST_FRAGMENT_POS -> HomeFragment.newInstance(TAB_ONE)
            SECOND_FRAGMENT_POS -> HomeFragment.newInstance(TAB_TWO)
            THIRD_FRAGMENT_POS -> HomeFragment.newInstance(TAB_THREE)

            else -> HomeFragment.newInstance(TAB_ONE)
        }
    }

    override fun getCount(): Int {
        return SCREENS_NO
    }
}