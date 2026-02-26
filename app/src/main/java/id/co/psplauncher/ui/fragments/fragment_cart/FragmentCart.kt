package id.co.psplauncher.ui.fragments.fragment_cart

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentCartBinding
import id.co.psplauncher.ui.payment.PaymentFragment

@AndroidEntryPoint
class FragmentCart : BottomSheetDialogFragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FragmentCartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("dismiss_cart", Bundle())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = emptyList(),
            onQuantityChanged = { productId, newQuantity ->
                viewModel.updateQuantity(productId, newQuantity)
            },
            onItemRemoved = { productId ->
                viewModel.removeItem(productId)
            },
            context = requireContext()
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.updateData(items)
            updateTotal()
            binding.rvCartItems.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.postCartResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.btnPay.isEnabled = false
                    binding.btnPay.alpha = 0.6f
                }
                is Resource.Success -> {
                    binding.btnPay.isEnabled = true
                    binding.btnPay.alpha = 1f
                    val cartId = result.value
                    dismiss()
                    val bundle = Bundle().apply { putString("cartId", cartId) }
                    val paymentFragment = PaymentFragment().apply { arguments = bundle }
                    // Gunakan fragmentManager dari Activity agar bisa replace container utama
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, paymentFragment)
                        .addToBackStack(null)
                        .commit()
                }
                is Resource.Failure -> {
                    binding.btnPay.isEnabled = true
                    binding.btnPay.alpha = 1f
                    Toast.makeText(requireContext(), "Gagal memproses pesanan, coba lagi", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun updateTotal() {
        val total = viewModel.getTotal()
        binding.tvTotalValue.text = formatCurrency(total)
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.layoutPayment.setOnClickListener {
            viewModel.postShoppingCart()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}