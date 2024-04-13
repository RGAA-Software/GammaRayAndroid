package com.tc.client.ui.base

interface OnListItemListener<T> {
    fun onItemClicked(pos: Int, value: T);
}