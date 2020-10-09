package com.jerrwu.yomikatawa

import java.util.regex.Pattern

object StringHelper {
    val hiragana = listOf<String>(
        ""
    )

    fun nullToEmpty(string: String?): String {
        if (string == null) {
            return ""
        }
        return string
    }

    fun hasAlphaNumbers(string: String): Boolean {
        return Pattern.compile( "[0-9]" ).matcher(string).find() or
                Pattern.compile( "[a-zA-Z]+" ).matcher(string).find()
    }

    fun onlyKana(string: String): Boolean {
        return Pattern.compile( "^[\u3040-\u30FF]+\$" ).matcher(string).find()
    }
}