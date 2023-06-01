package net.ghiassy.sftp_uploader.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ButtonViewModel : ViewModel() {
    private val _buttonEnabled = MutableLiveData<Boolean>()
    val buttonEnabled: LiveData<Boolean> = _buttonEnabled

    fun setButtonEnabled(enabled: Boolean) {
        _buttonEnabled.value = enabled
    }
}
