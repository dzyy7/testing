package id.co.psplauncher.ui.fragments.dialog_logout

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.databinding.FragmentBottomSheetLogoutBinding

@AndroidEntryPoint
class BottomSheetLogout : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetLogoutBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_KEY = "logout_request"
        const val RESULT_LOGOUT = "result_logout"
        
        fun newInstance() = BottomSheetLogout()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme).apply {
            setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetLogoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnTidak.setOnClickListener {
            dismiss()
        }

        binding.btnYa.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_LOGOUT to true))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
