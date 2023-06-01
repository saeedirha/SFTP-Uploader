package net.ghiassy.sftp_uploader.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private val _data = MutableLiveData<ArrayList<ServerModel>>().apply {
        value = ArrayList()
    }
    val data: LiveData<ArrayList<ServerModel>> = _data

    fun updateList(updatedList: ArrayList<ServerModel>) {
        _data.value = updatedList
    }

}