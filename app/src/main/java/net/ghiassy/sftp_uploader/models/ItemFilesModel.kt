package net.ghiassy.sftp_uploader.models

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import net.ghiassy.sftp_uploader.utils.FileDetails

class ItemFilesModel(private val context: Context, private val fileUri: Uri) {

    private val document = DocumentFile.fromSingleUri(context, fileUri)

    fun getFileSize(): String {
        if (document != null && document.isFile) {
            return FileDetails.getHumanReadableFileSize(document.length())
        }
        return "Unknown!"
    }
    fun getFileSizeLong(): Long {
        if (document != null && document.isFile) {
            return document.length()
        }
        return 0
    }

    fun getFileName(): String {
        if (document != null && document.isFile) {
            return document.name!!
        }
        return "Filename unknown!"
    }

    fun getFileUri(): Uri {
        return fileUri
    }
}