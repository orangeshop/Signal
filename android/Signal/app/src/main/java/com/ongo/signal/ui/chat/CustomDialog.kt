package com.ongo.signal.ui.chat

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.ongo.signal.R

object CustomDialog {
    fun show(context: Context, onSuccess: () -> Unit) {
        // 다이얼로그 레이아웃을 인플레이트합니다.
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.chat_custom_dialog, null)

        // 다이얼로그 빌더를 사용하여 커스텀 레이아웃을 설정합니다.
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)

        // UI 요소를 참조합니다.
        val title: TextView = dialogView.findViewById(R.id.dialog_title)

        val dialog = builder.create()

        title.setOnClickListener {
            // 삭제
            onSuccess()

            dialog.dismiss()

        }

        // 다이얼로그를 생성하고 표시합니다.

        dialog.show()
    }




}