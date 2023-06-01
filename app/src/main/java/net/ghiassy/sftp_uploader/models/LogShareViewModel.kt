package net.ghiassy.sftp_uploader.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogShareViewModel: ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    init {
        _text.value = ""
    }

    fun appendText(newText: String) {
        _text.value = _text.value + newText
    }

    fun clearText() {
        _text.value = ""
    }
}