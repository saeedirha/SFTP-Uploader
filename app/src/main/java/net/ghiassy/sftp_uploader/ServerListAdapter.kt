package net.ghiassy.sftp_uploader

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.ghiassy.sftp_uploader.models.ServerModel

class ServerListAdapter(
    private val mList: ArrayList<ServerModel>,
    private val mClickListener: OnItemClickListener,
    private val mLongClickListener: OnItemLongClickListener
)
    :RecyclerView.Adapter<ServerListAdapter.ServerViewHolder>()
{
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int): Boolean
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.server_card, parent, false)
        return ServerViewHolder(view)

    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {

        val currentItem = mList[position]

        holder.host.text = currentItem.host
        holder.port.text = "Port: ${currentItem.port.toShort()}"
        holder.protocol.text = "Protocol: ${currentItem.protocol}"
        holder.enabled.isChecked = currentItem.isEnabled

        holder.itemView.setOnClickListener { mClickListener.onItemClick(position) }
        holder.itemView.setOnLongClickListener { mLongClickListener.onItemLongClick(position) }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun deleteItem(position: Int) {
        mList.removeAt(position)
        Log.d("++++++++++>", mList.size.toString())
        //notifyItemRemoved(position)
       // notifyDataSetChanged()
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    fun addItem(position: Int, item: ServerModel) {
        mList.add(position, item)
        Log.d("++++++++++>", mList.size.toString())
        notifyItemInserted(position)
    }

    inner class ServerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val host : TextView = ItemView.findViewById(R.id.txtServerName)
        val port : TextView = ItemView.findViewById(R.id.txtPort)
        val protocol : TextView = ItemView.findViewById(R.id.txtProtocol)
        val enabled: MaterialCardView = ItemView.findViewById(R.id.ServerCardView)



    }
}