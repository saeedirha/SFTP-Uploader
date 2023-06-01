package net.ghiassy.sftp_uploader.models

import net.ghiassy.sftp_uploader.utils.FileDetails

class ItemFilesModel(val filename: String) {

    fun getFileSize(): String {
        return FileDetails.getHumanReadableFileSize(filename)
    }
}