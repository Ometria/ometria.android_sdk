package com.android.ometriapoc.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.android.ometriapoc.R
import kotlinx.android.synthetic.main.activity_main.*

private val TAG = MainActivity::class.simpleName

private const val OFF_SCREEN_PAGE_LIMIT = 3
const val FIRST_FRAGMENT_POS = 0
const val SECOND_FRAGMENT_POS = 1
const val THIRD_FRAGMENT_POS = 2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        detailsBTN.setOnClickListener {
//            startActivity(Intent(this, DetailsActivity::class.java))
//        }

        setUpBottomNavMenu()
        setupViewPager()
    }

    private fun switchFragment(position: Int) {
        containerVP.currentItem = position
    }

    private fun setUpBottomNavMenu() {
        bottomMenuBnv.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.restaurants -> {
                    switchFragment(FIRST_FRAGMENT_POS)
                }
                R.id.my_orders -> {
                    switchFragment(SECOND_FRAGMENT_POS)
                }
                R.id.my_profile -> {
                    switchFragment(THIRD_FRAGMENT_POS)
                }
            }
            false
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val firstFragment = HomeFragment()
        val secondFragment = HomeFragment()
        val thirdFragment = HomeFragment()
        adapter.addFragment(firstFragment)
        adapter.addFragment(secondFragment)
        adapter.addFragment(thirdFragment)
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
}