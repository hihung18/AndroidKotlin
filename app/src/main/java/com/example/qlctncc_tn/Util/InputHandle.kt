package com.example.qlctncc_tn.Util;
import android.text.InputFilter
import android.text.Spanned

object InputHandle {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (Character.isWhitespace(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }
}

