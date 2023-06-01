package net.ghiassy.sftp_uploader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.ghiassy.sftp_uploader.databinding.FragmentLogBinding
import net.ghiassy.sftp_uploader.models.LogShareViewModel


class LogsFragment : Fragment() {

    private val logSharedViewModel: LogShareViewModel by activityViewModels()

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLogBinding.inflate(inflater, container, false)
        val view = binding.root

        logSharedViewModel.text.observe(viewLifecycleOwner) {
            binding.logEditText.setText(it + "\n")

        }
        binding.btnClearLog.setOnClickListener {
            logSharedViewModel.clearText()
            binding.logEditText.setText("")

        }
        return view
    }

}