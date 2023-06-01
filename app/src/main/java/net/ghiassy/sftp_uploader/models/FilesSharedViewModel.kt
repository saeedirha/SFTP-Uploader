package net.ghiassy.sftp_uploader.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilesSharedViewModel: ViewModel() {

    private val _data = MutableLiveData<ArrayList<ItemFilesModel>>()
    val data: LiveData<ArrayList<ItemFilesModel>> = _data

    init {
        //Generating random data for list
        _data.value = ArrayList<ItemFilesModel>()
        //---------------------------------------------
    }
    fun updateList(updatedList: ArrayList<ItemFilesModel>) {
        _data.value = updatedList
    }

}