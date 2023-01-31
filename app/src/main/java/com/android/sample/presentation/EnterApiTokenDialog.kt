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
import com.android.sample.databinding.DialogEnterApiTokenBinding

class EnterApiTokenDialog : DialogFragment() {

    private lateinit var binding: DialogEnterApiTokenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEnterApiTokenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apiTokenET.setText(AppPreferencesUtils.getApiToken() ?: "")
        binding.saveAPIKeyBTN.setOnClickListener {
            val apiToken = binding.apiTokenET.text.toString()
            Ometria.initialize(
                application = SampleApp.instance,
                apiToken = apiToken,
                notificationIcon = R.drawable.ic_notification_nys,
                notificationColor = ContextCompat.getColor(requireContext(), R.color.colorAccent),
                notificationChannelName = "Custom Channel Name"
            ).loggingEnabled(true)
            AppPreferencesUtils.saveApiToken(apiToken)
            dismiss()
        }
        binding.cancelBTN.setOnClickListener { dismiss() }
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