package id.co.psplauncher.ui.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.databinding.FragmentPosBinding
import id.co.psplauncher.ui.fragments.fragment_cart.FragmentCart
import id.co.psplauncher.ui.fragments.fragment_draft.FragmentDraft
import id.co.psplauncher.ui.fragments.item_detail.FragmentItemDetail
import id.co.psplauncher.ui.main.MainActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPos : Fragment() {
    private var _binding: FragmentPosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FragmentPosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        setupSwipeRefresh()
        observeCart()
    }

    private fun setupUI() {
        viewModel.setupBalanceCarousel(viewLifecycleOwner, requireContext(), binding)
        viewModel.setupCategoryRecycler(viewLifecycleOwner, requireContext(), binding)
        viewModel.setupProductRecycler(viewLifecycleOwner, requireContext(), binding) { product ->
            showProductDetail(product.id)
        }
        viewModel.setupViewToggle(viewLifecycleOwner, requireContext(), binding)
        viewModel.setupSearchListener(binding)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            android.R.color.holo_purple,
            android.R.color.holo_blue_bright
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshAll()
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
    }

    private fun setupClickListeners() {
        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.bottomCartBar.setOnClickListener {
            showCart()
        }

        binding.btnDraft.setOnClickListener {
            showDraft()
        }

        binding.btnSort.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(requireContext(), view)
            popup.menu.apply {
                add(0, 0, 0, "A-Z")
                add(0, 2, 2, "Z-A")
                add(0, 3, 3, "Harga Terendah")
                add(0, 4, 4, "Harga Tertinggi")
            }
            popup.setOnMenuItemClickListener { item ->
                val (sortType, label) = when (item.itemId) {
                    2 -> FragmentPosViewModel.SortType.NAME_DESC to "Z-A"
                    3 -> FragmentPosViewModel.SortType.PRICE_ASC to "Terendah"
                    4 -> FragmentPosViewModel.SortType.PRICE_DESC to "Tertinggi"
                    else -> FragmentPosViewModel.SortType.DEFAULT to "A-Z"
                }
                viewModel.sortBy(sortType)
                binding.btnSort.text = label
                true
            }
            popup.show()
        }

        binding.btnNotification.setOnClickListener {
            // TODO: Handle notification click
        }
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            CartManager.cartItems.collect { cartItems ->
                val itemCount = cartItems.sumOf { it.quantity }
                val totalPrice = cartItems.sumOf { it.totalPrice }

                binding.tvItemCount.text = "$itemCount Item Selected"
                binding.tvTotalPrice.text = "Rp. ${formatCurrency(totalPrice)}"

                viewModel.refreshAdapterCartState()
            }
        }
    }

    private fun showProductDetail(productId: String) {
        dimBackground(true)
        val dialog = FragmentItemDetail.newInstance(productId)
        dialog.show(childFragmentManager, "FragmentItemDetail")

        childFragmentManager.setFragmentResultListener("dismiss_item_detail", viewLifecycleOwner) { _, _ ->
            dimBackground(false)
        }
    }

    private fun showCart() {
        dimBackground(true)
        val dialog = FragmentCart()
        dialog.show(childFragmentManager, "FragmentCart")

        childFragmentManager.setFragmentResultListener("dismiss_cart", viewLifecycleOwner) { _, _ ->
            dimBackground(false)
        }
    }

    private fun showDraft() {
        dimBackground(true)
        val dialog = FragmentDraft()
        dialog.show(childFragmentManager, "FragmentDraft")

        childFragmentManager.setFragmentResultListener("dismiss_draft", viewLifecycleOwner) { _, _ ->
            dimBackground(false)
        }
    }

    private fun dimBackground(dim: Boolean) {
        if (dim) {
            binding.dimOverlay.visibility = View.VISIBLE
            binding.dimOverlay.animate()
                .alpha(1.0f)
                .setDuration(200)
                .start()
        } else {
            binding.dimOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    _binding?.dimOverlay?.visibility = View.GONE
                }
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}