package com.tc.client.ui.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tc.client.AppContext
import com.tc.client.databinding.FragmentAboutmeBinding
import com.tc.client.ui.BaseFragment

class AboutMeFragment() : BaseFragment() {

    private lateinit var binding: FragmentAboutmeBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutmeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}