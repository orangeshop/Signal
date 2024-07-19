package com.ongo.signal.util

import android.content.Context
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import com.ongo.signal.R
import com.ongo.signal.data.model.main.PostDTO

object UserPopupMenuHelper {
    fun showUserPopupMenu(
        context: Context,
        view: View,
        post: PostDTO,
        onChatClicked: (PostDTO) -> Unit
    ) {
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.popup_chat_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_chat -> {
                    onChatClicked(post)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }
}