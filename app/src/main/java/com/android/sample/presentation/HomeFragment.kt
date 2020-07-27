package com.android.sample.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.sample.R
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

private const val POSITION_KEY = "position_key"
const val TAB_ONE = 0
const val TAB_TWO = 1
const val TAB_THREE = 2

class HomeFragment : Fragment() {

    private var screenPosition = TAB_ONE

    companion object {
        fun newInstance(position: Int): HomeFragment {
            val instance = HomeFragment()
            instance.arguments = Bundle().apply {
                putInt(POSITION_KEY, position)
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

        screenPosition = arguments?.getInt(POSITION_KEY)!!

        detailsBTN.setOnClickListener {
            startActivity(Intent(requireContext(), DetailsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        when (screenPosition) {
            TAB_ONE -> {
                screenTitleTV.text = "List of Products"
                detailsBTN.visibility = VISIBLE

                Ometria.instance()
                    .trackEvent(OmetriaEventType.VIEW_SCREEN, "List of Products screen")
            }

            TAB_TWO -> {
                screenTitleTV.text = "Orders"
                detailsBTN.visibility = GONE

                Ometria.instance()
                    .trackEvent(OmetriaEventType.VIEW_SCREEN, "Orders screen")
            }

            TAB_THREE -> {
                screenTitleTV.text = "My Profile"
                detailsBTN.visibility = GONE

                Ometria.instance()
                    .trackEvent(OmetriaEventType.VIEW_SCREEN, "My Profile screen")
            }
        }
    }
}