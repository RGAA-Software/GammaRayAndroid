package com.tc.client.ui.tab

import androidx.compose.runtime.Composable
import com.tc.client.R

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    object Music : TabItem(R.drawable.ic_launcher, "Music", { MusicScreen() })
    object Movies : TabItem(R.drawable.ic_launcher, "Movies", { MoviesScreen() })
    object Books : TabItem(R.drawable.ic_launcher, "Books", { BooksScreen() })
}