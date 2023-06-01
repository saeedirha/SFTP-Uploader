package net.ghiassy.sftp_uploader.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UploadProgressShareModel: ViewModel() {

    private val _text = MutableLiveData<String>()
    private val _progress = MutableLiveData<Int>()
    private val _isDone = MutableLiveData<Boolean>()
    private val _isError = MutableLiveData<Boolean>()
    val text: LiveData<String> get() = _text
    val progress: LiveData<Int> get() = _progress
    val isDone: LiveData<Boolean> get() = _isDone
    val isError: LiveData<Boolean> get() = _isError

    init {
        _text.value = ""
        _progress.value = 0
        _isDone.value = false
        _isError.value = false
    }

    fun updateText(newText: String) {
        _text.value = newText
    }

    fun done(done:Boolean){
       _isDone.value = done

    }
    fun updateProgress(newProgress: Int) {
        _progress.value = newProgress
    }

    fun reset(){
        _text.value = ""
        _progress.value = 0
        _isDone.value = false
        _isError.value = false
    }

    fun error(){
        _isError.value = true
    }

}