package id.co.psplauncher.ui.fragments.fragment_draft

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentDraftBinding

@AndroidEntryPoint
class FragmentDraft : BottomSheetDialogFragment() {

    private var _binding: FragmentDraftBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DraftViewModel by viewModels()
    private lateinit var draftAdapter: DraftAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("dismiss_draft", Bundle())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        viewModel.fetchDrafts()
    }

    private fun setupRecyclerView() {
        draftAdapter = DraftAdapter(
            items = emptyList(),
            onDraftClick = { draftItem ->
                if (viewModel.isCartEmpty()) {
                    viewModel.loadDraftIntoCart(draftItem.id)
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Ganti Pesanan?")
                        .setMessage("Cart saat ini akan diganti dengan draft \"${draftItem.draftName}\". Lanjutkan?")
                        .setPositiveButton("Ya, Ganti") { _, _ ->
                            viewModel.loadDraftIntoCart(draftItem.id)
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            },
            onDeleteClick = { draftItem ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Draft?")
                    .setMessage("Draft \"${draftItem.draftName}\" akan dihapus permanen. Lanjutkan?")
                    .setPositiveButton("Hapus") { _, _ ->
                        viewModel.deleteDraft(draftItem.id)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        binding.rvDrafts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = draftAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.drafts.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBarDraft.visibility = View.VISIBLE
                    binding.rvDrafts.visibility = View.GONE
                    binding.layoutEmptyDraft.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBarDraft.visibility = View.GONE
                    val drafts = state.value
                    if (drafts.isEmpty()) {
                        binding.layoutEmptyDraft.visibility = View.VISIBLE
                        binding.rvDrafts.visibility = View.GONE
                    } else {
                        binding.layoutEmptyDraft.visibility = View.GONE
                        binding.rvDrafts.visibility = View.VISIBLE
                        draftAdapter.updateData(drafts)
                    }
                }
                is Resource.Failure -> {
                    binding.progressBarDraft.visibility = View.GONE
                    binding.layoutEmptyDraft.visibility = View.VISIBLE
                    binding.rvDrafts.visibility = View.GONE
                }
                else -> Unit
            }
        }

        viewModel.loadDraftResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> { }
                is Resource.Success -> dismiss()
                is Resource.Failure -> {
                    android.widget.Toast.makeText(
                        requireContext(), "Gagal memuat draft, coba lagi", android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                else -> Unit
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBarDraft.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBarDraft.visibility = View.GONE
                    android.widget.Toast.makeText(
                        requireContext(), "Draft berhasil dihapus", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    // List otomatis di-refresh dari ViewModel
                }
                is Resource.Failure -> {
                    binding.progressBarDraft.visibility = View.GONE
                    android.widget.Toast.makeText(
                        requireContext(), "Gagal menghapus draft", android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                else -> Unit
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCloseDraft.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}