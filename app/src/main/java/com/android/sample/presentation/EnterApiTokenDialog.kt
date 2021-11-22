package com.android.sample.presentation

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.android.ometriasdk.core.Ometria
import com.android.sample.R
import com.android.sample.SampleApp
import com.android.sample.data.AppPreferencesUtils
import kotlinx.android.synthetic.main.dialog_enter_api_token.*

class EnterApiTokenDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_enter_api_token, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiTokenET.setText(AppPreferencesUtils.getApiToken() ?: "")
        saveAPIKeyBTN.setOnClickListener {
            val apiToken = apiTokenET.text.toString()
            Ometria.initialize(
                SampleApp.instance,
                apiToken,
                R.drawable.ic_notification_nys,
                ContextCompat.getColor(requireContext(), R.color.colorAccent)
            ).loggingEnabled(true)
            AppPreferencesUtils.saveApiToken(apiToken)
            dismiss()
        }
        cancelBTN.setOnClickListener { dismiss() }
    }

    override fun onResume() {
        super.onResume()
        val displayRectangle = Rect()
        dialog?.window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)

        val params = dialog?.window?.attributes
        params?.width = (displayRectangle.width() * DIALOG_SCREEN_RATIO).toInt()
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
    }

    companion object {
        private const val DIALOG_SCREEN_RATIO = 0.90f
    }
}