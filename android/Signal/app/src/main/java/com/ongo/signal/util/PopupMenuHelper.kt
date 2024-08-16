package com.ongo.signal.util

import android.content.Context
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu

object PopupMenuHelper {
    fun showPopupMenu(
        context: Context,
        view: View,
        menuResId: Int,
        onMenuItemClick: (MenuItem) -> Boolean
    ) {
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(menuResId, popup.menu)
        popup.setOnMenuItemClickListener(onMenuItemClick)
        popup.show()
    }
}