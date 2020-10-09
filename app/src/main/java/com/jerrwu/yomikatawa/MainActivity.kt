package com.jerrwu.yomikatawa

import android.animation.AnimatorInflater
import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


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

        if (StringHelper.hasAlphaNumbers(text)) {
            Toast.makeText(this, R.string.alpha_number_error, Toast.LENGTH_LONG).show()
            return
        }

        if (StringHelper.onlyKana((text))) {
            Toast.makeText(this, R.string.no_kanji_error, Toast.LENGTH_LONG).show()
            return
        }

        search_edit_text.clearFocus()
    }
}