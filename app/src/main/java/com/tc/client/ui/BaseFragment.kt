package com.tc.client.ui

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.tc.client.App
import com.tc.client.AppContext

open class BaseFragment(private val hostActivity: Activity): Fragment() {
    public var appContext: AppContext = (hostActivity.application as App).appContext
}