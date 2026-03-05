package id.co.psplauncher.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentNotificationBinding
import id.co.psplauncher.ui.pos.FragmentPos

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        viewModel.loadNotifications()
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(
            items = emptyList(),
            onItemClick = { notification ->
                navigateToDetail(notification.id)
            }
        )
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@NotificationFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.notifications.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    // Show loading if needed
                }
                is Resource.Success -> {
                    adapter.updateData(state.value.content)
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Gagal memuat notifikasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            // Explicit balik ke POS, bukan sekedar pop back stack
            val posFragment = FragmentPos()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, posFragment)
                .commit()
        }
    }

    private fun navigateToDetail(id: String) {
        val detailFragment = NotificationDetailFragment.newInstance(id)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
