package com.tc.client.ui.me

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tc.client.BuildConfig
import com.tc.client.Settings
import com.tc.client.databinding.FragmentSettingsBinding
import com.tc.client.ui.BaseFragment

class SettingsFragment() : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding;

    companion object {
        const val TAG = "Main"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.idShowVirtualGamepad.isChecked = Settings.getInstance().showVirtualGamepad
        binding.idShowVirtualGamepad.setOnCheckedChangeListener { buttonView, isChecked ->
            context?.let { Settings.getInstance().setShowVirtualGamepad(it, isChecked) }
        }

        binding.idInvertJoystickY.isChecked = Settings.getInstance().invertJoystickYAxis
        binding.idInvertJoystickY.setOnCheckedChangeListener { buttonView, isChecked ->
            context?.let { Settings.getInstance().setInvertJoystickYAxis(it, isChecked) }
        }

        binding.idShowCursor.isChecked = Settings.getInstance().showCursor
        binding.idShowCursor.setOnCheckedChangeListener { buttonView, isChecked ->
            context?.let { Settings.getInstance().setShowCursor(it, isChecked) }
        }

        binding.idFullscreen.isChecked = Settings.getInstance().fullscreen
        binding.idFullscreen.setOnCheckedChangeListener { buttView, isChecked ->
            context?.let { Settings.getInstance().setFullscreen(it, isChecked) }
        }

        binding.idVersion.setText("V " + BuildConfig.VERSION_NAME)

    }

}