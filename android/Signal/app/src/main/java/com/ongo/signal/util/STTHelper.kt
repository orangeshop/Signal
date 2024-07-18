package com.ongo.signal.util

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import java.util.Locale

class STTHelper(private val activity: Activity, private val requestCode: Int) {
    fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "검색어를 말해주세요.")

        try {
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?, callback: (String) -> Unit) {
        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            callback(result?.get(0).toString())
        }
    }
}