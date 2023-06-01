package net.ghiassy.sftp_uploader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.ghiassy.sftp_uploader.databinding.FragmentServerListBinding
import net.ghiassy.sftp_uploader.models.ServerModel
import net.ghiassy.sftp_uploader.models.SharedViewModel


class ServerListFragment : Fragment() ,
    ServerListAdapter.OnItemClickListener,
    ServerListAdapter.OnItemLongClickListener{

    private lateinit var  data : ArrayList<ServerModel>

    private lateinit var sharedViewModel: SharedViewModel


    private var _binding: FragmentServerListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        data = sharedViewModel.data.value!!

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentServerListBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.serverListRecyclerView.layoutManager  = LinearLayoutManager(context)

        sharedViewModel.data.observe(viewLifecycleOwner) { mData ->
            // Handle the new value of the sharedData property
            data = mData
//            Log.d("------------->", data.size.toString())
            //Toast.makeText(context, "Data changed", Toast.LENGTH_LONG).show()
        }


        val adapter = ServerListAdapter(data,this,this)

        binding.serverListRecyclerView.adapter = adapter

        val SwipeGesture = object : SwipeGesture(this.requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        adapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(SwipeGesture)
        itemTouchHelper.attachToRecyclerView(binding.serverListRecyclerView)

        binding.addServerButton.setOnClickListener(View.OnClickListener {

            NewServerAddFragment().show(childFragmentManager, "NewServerAddFragment")
        })

        return view
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Long click to enable the server", Toast.LENGTH_SHORT).show()
        data.get(position).isEnabled = false
        binding.serverListRecyclerView.adapter?.notifyItemChanged(position)
    }

    override fun onItemLongClick(position: Int): Boolean {
        data.get(position).isEnabled = true
        binding.serverListRecyclerView.adapter?.notifyItemChanged(position)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("ServerListFragment", "ServerListFragment")
    }


}