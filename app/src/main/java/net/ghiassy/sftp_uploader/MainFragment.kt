package net.ghiassy.sftp_uploader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ghiassy.sftp_uploader.databinding.FragmentMainBinding
import net.ghiassy.sftp_uploader.models.ButtonViewModel
import net.ghiassy.sftp_uploader.models.FilesSharedViewModel
import net.ghiassy.sftp_uploader.models.ItemFilesModel
import net.ghiassy.sftp_uploader.models.LogShareViewModel
import net.ghiassy.sftp_uploader.models.UploadProgressShareModel
import net.ghiassy.sftp_uploader.utils.FileUtils

class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    private val filesSharedViewModel: FilesSharedViewModel by activityViewModels()
    private val uploadProgressShareModel: UploadProgressShareModel by activityViewModels()
    private val buttonViewModel: ButtonViewModel by activityViewModels()
    private val logSharedViewModel: LogShareViewModel by activityViewModels()



    private var mList = ArrayList<ItemFilesModel>()
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // When user selectes multipe files
                val data: Intent? = result.data
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount
                    var currentItem = 0
                    while (currentItem < count!!) {
                        val fileUri: Uri? = data.clipData?.getItemAt(currentItem)?.uri
                        val path: String = FileUtils.getPath(requireContext(), fileUri)
                        mList.add(ItemFilesModel(path))
                        filesSharedViewModel.updateList(mList)
                        logSharedViewModel.appendText("Added file: $path\n")
                        Log.i("=====??=====>", path)
                        currentItem++
                    }
                    logSharedViewModel.appendText("----------------------------\n")
                    // When user only select one file
                } else if (data?.data != null) {
                    try {
                        val path = FileUtils.getPath(requireContext(), data.data)
                        mList.add(ItemFilesModel(path))
                        filesSharedViewModel.updateList(mList)
                        logSharedViewModel.appendText("Added file: $path\n")
                        logSharedViewModel.appendText("----------------------------\n")
                    } catch (e: NullPointerException) {
                        Toast.makeText(
                            context,
                            "Error: File cannot be added.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, e.message.toString())
                    }
                }
            }
            binding.fileListRecyclerView.adapter?.notifyDataSetChanged()
        }


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Essential part if you want the data to survive
//        //rotation of the screen
//        filesSharedViewModel =
//            ViewModelProvider(requireActivity()).get(FilesSharedViewModel::class.java)
        mList = filesSharedViewModel.data.value!!


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "Value of Card item: " + uploadProgressShareModel.text.value)


        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.fileListRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = FilesListAdapter(filesSharedViewModel.data)



        binding.fileListRecyclerView.adapter = adapter


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
        itemTouchHelper.attachToRecyclerView(binding.fileListRecyclerView)

        uploadProgressShareModel.text.observe(viewLifecycleOwner) {

           val v =  binding.fileListRecyclerView.findViewHolderForAdapterPosition(0) as? FilesListAdapter.ViewHolder
            v?.status?.text = it
        }

        uploadProgressShareModel.progress.observe(viewLifecycleOwner) {
            val v =  binding.fileListRecyclerView.findViewHolderForAdapterPosition(0) as? FilesListAdapter.ViewHolder

            val filename  = v?.fileName?.text.toString()
            v?.spinKitView?.visibility = View.VISIBLE
            if (it == 100) {
                uploadProgressShareModel.reset()
                v?.spinKitView?.visibility = View.GONE

                //Giving sometime for ReceyclerView to update
                lifecycleScope.launch {
                    delay(2000L)
                }
                Log.d(TAG,"Value of done: ${uploadProgressShareModel.isDone.value}")
                //adapter.deleteItem(0)
                return@observe
            }
            v?.status?.text = "Uploaded: ${it.toString()}%"
        }
        uploadProgressShareModel.isError.observe(viewLifecycleOwner) {
            if (it) {
                val v =  binding.fileListRecyclerView.findViewHolderForAdapterPosition(0) as? FilesListAdapter.ViewHolder
                v?.spinKitView?.visibility = View.GONE
                v?.status?.text = "Error: Could not upload file to all servers!"
            }
        }
        uploadProgressShareModel.isDone.observe(viewLifecycleOwner) {
            if (it) {
                //TODO: possible problem here
                uploadProgressShareModel.reset()
                Log.d(TAG,"Value of done: $it")
                logSharedViewModel.appendText("File ${mList.get(0).filename} uploaded successfully\n")
                adapter.deleteItem(0)
            }
        }


        binding.addFilesButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "*/*"

            resultLauncher.launch(intent)

            buttonViewModel.setButtonEnabled(true)
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("MainFragment", "MainFragment")
    }

}