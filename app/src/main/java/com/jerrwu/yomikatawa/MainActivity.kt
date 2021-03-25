package com.jerrwu.yomikatawa

import android.animation.AnimatorInflater
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainActivity : AppCompatActivity() {
    companion object{
        final const val EDITTEXT_UNFOCUSED_WIDTH = 240
        final const val EDITTEXT_FOCUSED_WIDTH = 360
    }

    private var originalWidth: Int = 0
    private var originalHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        originalWidth = search_edit_text.layoutParams.width
        originalHeight = search_edit_text.layoutParams.height

        (this.findViewById(R.id.main_activity) as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    override fun onResume() {
        super.onResume()

        search_edit_text.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> onTextEntered()
            }
            false
        })

        search_edit_text.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                search_edit_text.stateListAnimator =
                    AnimatorInflater.loadStateListAnimator(this, R.animator.elevation_focused)
                search_edit_text.layoutParams.width = (originalWidth * 1.3).toInt()
                search_edit_text.layoutParams.height = (originalHeight * 1.1).toInt()
            } else {
                search_edit_text.stateListAnimator =
                    AnimatorInflater.loadStateListAnimator(this, R.animator.elevation_unfocused)
                search_edit_text.layoutParams.width = originalWidth
                search_edit_text.layoutParams.height = originalHeight
            }
        }
    }

    private fun onTextEntered(){
        val text = StringHelper.nullToEmpty(search_edit_text.text.toString())

        if (text.isEmpty()) return

        if (StringHelper.hasAlphaNumbers(text)) {
            Toast.makeText(this, R.string.alpha_number_error, Toast.LENGTH_LONG).show()
            return
        }

        if (StringHelper.onlyKana((text))) {
            Toast.makeText(this, R.string.no_kanji_error, Toast.LENGTH_LONG).show()
            return
        }

        search_edit_text.clearFocus()
        fetchYomikata(text)
    }

    @SuppressLint("CheckResult")
    private fun fetchYomikata(string: String) {
        progress.visibility = View.VISIBLE
        Single.fromCallable{
            val encoded = Uri.encode(string)
            val doc = Jsoup.connect("https://yomikatawa.com/kanji/$encoded?search=1").get()
            return@fromCallable doc
        }
            .onErrorReturn { Document("") }
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { html: Document ->
                    if (html.baseUri().isEmpty()) {
                        fetchError()
                        return@subscribe
                    }

                    val kana = html.body()
                        .getElementById("content")
                        .getElementsByTag("p")[0]
                        .text()
                    kanaRetrieved(kana)
                }
    }

    private fun kanaRetrieved(kana: String) {
        kana_text.text = kana
        progress.visibility = View.GONE
    }

    private fun fetchError() {
        Toast.makeText(this, R.string.not_found_error, Toast.LENGTH_LONG).show()
        progress.visibility = View.GONE
    }
}