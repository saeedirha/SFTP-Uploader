package net.ghiassy.sftp_uploader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import net.ghiassy.sftp_uploader.databinding.FragmentNewServerAddBinding
import net.ghiassy.sftp_uploader.models.ServerModel
import net.ghiassy.sftp_uploader.models.SharedViewModel
import net.ghiassy.sftp_uploader.utils.InputFilterUtils
import net.ghiassy.sftp_uploader.utils.TestServerConnections


class NewServerAddFragment : DialogFragment() {

    private val TAG = "NewServerAddFragment"

    private var _binding: FragmentNewServerAddBinding? = null
    private val binding get() = _binding!!


    private lateinit var sharedViewModel: SharedViewModel
    private var protocol = "SFTP"
    private var isTested = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewServerAddBinding.inflate(inflater, container, false)
        val view = binding.root

        val itemsArray = resources.getStringArray(R.array.server_protocol)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, itemsArray)

        binding.txtProtocol.setAdapter(adapter)
        binding.txtProtocol.setText(itemsArray[0], false)

        val protocolToPortMap = mapOf(
            "SFTP" to "22"
        )
        binding.txtProtocol.setOnItemClickListener { parent, view, position, id ->
            protocol = parent.getItemAtPosition(position).toString()
            binding.txtPort.setText(protocolToPortMap[protocol])
        }

        //Port range filter
        val filter = InputFilterUtils.createInputFilter()
        binding.txtPort.filters = arrayOf(filter)


        binding.btnTestConnection.setOnClickListener(View.OnClickListener {
            binding.spinKit.visibility = View.VISIBLE
            binding.btnTestConnection.visibility = View.GONE

            val context = requireContext()

            lifecycleScope.launch(Dispatchers.Main) {
                val server: ServerModel = ServerModel(
                    binding.txtServerName.text.toString(),
                    binding.txtPort.text.toString().toInt(),
                    binding.txtUsername.text.toString(),
                    binding.txtPassword.text.toString(),
                    binding.txtProtocol.text.toString()
                )

                val operationDuration = withTimeoutOrNull(5000L) {
                    withContext(Dispatchers.IO) {
                        when (protocol) {
                            "SFTP" -> TestServerConnections.testSFTPConnection(
                                server.host,
                                server.port,
                                server.username,
                                server.password
                            )

                            else -> null
                        }
                    }
                }
                if (operationDuration != null) {
                    operationDuration.onSuccess {
                        Toast.makeText(
                            context,
                            "Test was successful :)",
                            Toast.LENGTH_SHORT
                        ).show()
                        isTested = true
                        binding.spinKit.visibility = View.GONE
                    }.onFailure {
                        Toast.makeText(context, "Test failed!!! " + it.message, Toast.LENGTH_LONG)
                            .show()
                        binding.spinKit.visibility = View.GONE
                        binding.btnTestConnection.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Timeout! Please check your connection",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.spinKit.visibility = View.GONE
                    binding.btnTestConnection.visibility = View.VISIBLE
                }
            } //end of lifecycleScope


        })

        binding.btnSave.setOnClickListener(View.OnClickListener {

            val fields = listOf(
                binding.txtServerName to "Please enter server name",
                binding.txtPort to "Required",
                binding.txtUsername to "Required",
                binding.txtPassword to "Required"
            )

            for ((field, errorMessage) in fields) {
                if (field.text.toString().isEmpty()) {
                    field.error = errorMessage
                    return@OnClickListener
                }
            }

            val item = ServerModel(
                binding.txtServerName.text.toString(),
                binding.txtPort.text.toString().toInt(),
                binding.txtUsername.text.toString(),
                binding.txtPassword.text.toString(),
                binding.txtProtocol.text.toString()
            )
            item.isTested = isTested

            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
            val currentList = sharedViewModel.data.value
            currentList?.add(item)
            sharedViewModel.updateList(currentList!!)
            dismiss()
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val layoutParams = window?.attributes
            layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            window?.attributes = layoutParams
        }
    }

}