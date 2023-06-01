package net.ghiassy.sftp_uploader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import net.ghiassy.sftp_uploader.models.ItemFilesModel

class FilesListAdapter(
    private val itemList: LiveData<ArrayList<ItemFilesModel>>) :
    RecyclerView.Adapter<FilesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.files_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemFilesModel = itemList.value?.get(position) ?: return

        var canAdd = true

        try {
            holder.fileName.text = itemFilesModel.filename
            holder.fileSize.text = itemFilesModel.getFileSize()

        } catch (e: Exception) {
            Toast.makeText(
                holder.itemView.context,
                "File cannot be added. Error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            canAdd = false
        }
        //remove the item from the list if cannot be added
        if (!canAdd) {
            //remove the item from the list
            //from the UI thread, once calculation of
            //recycler view is done
            holder.itemView.post(Runnable {
                deleteItem(position)
            })
        }
    }

    override fun getItemCount(): Int {
        return itemList.value?.size ?: 0
    }

    fun deleteItem(position: Int) {
        //both Removed and ChangedRange needs to be called
        //to update dataset properly
        //mList.removeAt(position)
        itemList.value?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)

    }


    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val fileName: TextView = ItemView.findViewById(R.id.txtFilePath)
        val fileSize: TextView = ItemView.findViewById(R.id.txtFileSize)
        val status: TextView = ItemView.findViewById(R.id.txtUploadStatus)
        val spinKitView: SpinKitView = ItemView.findViewById(R.id.spin_kit)
    }
}