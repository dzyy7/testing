package id.co.psplauncher.ui.notification

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentDetailNotificationBinding

@AndroidEntryPoint
class NotificationDetailFragment : Fragment() {

    private var _binding: FragmentDetailNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()

    companion object {
        private const val ARG_NOTIFICATION_ID = "notification_id"

        fun newInstance(id: String) = NotificationDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NOTIFICATION_ID, id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationId = arguments?.getString(ARG_NOTIFICATION_ID) ?: run {
            Toast.makeText(requireContext(), "ID notifikasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        setupObservers()
        setupClickListeners()
        
        viewModel.loadNotificationDetail(notificationId)
    }

    private fun setupObservers() {
        viewModel.notificationDetail.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    // Show loading if needed
                }
                is Resource.Success -> {
                    val detail = state.value
                    binding.tvNotifTitle.text = detail.type
                    binding.tvDate.text = detail.date
                    
                    // Render HTML message
                    binding.tvBody.text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Html.fromHtml(detail.message, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(detail.message)
                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Gagal memuat detail notifikasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
