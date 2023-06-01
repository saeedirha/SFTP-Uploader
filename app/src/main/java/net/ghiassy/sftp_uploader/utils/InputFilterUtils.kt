package net.ghiassy.sftp_uploader.utils

import android.text.InputFilter

object InputFilterUtils {
    fun createInputFilter(): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            val input = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) +
                    dest.subSequence(dend, dest.length).toString()
            val value = input.toIntOrNull()

            if (value != null && value in 1..65535) {
                null // Accept the input within the range
            } else {
                "" // Reject the input outside the range
            }
        }
    }
}
