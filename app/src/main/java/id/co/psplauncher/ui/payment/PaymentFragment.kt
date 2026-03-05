package id.co.psplauncher.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.databinding.FragmentPaymentBinding
import id.co.psplauncher.ui.fragments.dialog_paymentconfirmation.DialogPaymentConfirmation
import id.co.psplauncher.ui.payment.cash.CashPayment
import id.co.psplauncher.ui.payment.edc.EdcPayment
import id.co.psplauncher.ui.payment.qris.QrisPayment
import id.co.psplauncher.ui.payment.transfer.TransferPayment

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var cartAdapter: PaymentCartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartId = arguments?.getString("cartId") ?: run {
            Toast.makeText(requireContext(), "Cart ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        viewModel.loadCart(cartId)
    }

    private fun setupRecyclerView() {
        cartAdapter = PaymentCartAdapter(
            items = emptyList(),
            context = requireContext(),
            onQuantityChanged = { productId, newQuantity ->
                viewModel.updateItemQuantity(productId, newQuantity)
            }
        )
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupObservers() {
        viewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    cartAdapter.updateData(state.value.shoppingCartItems)
                    updateTotalUI()
                }
                is Resource.Failure -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Gagal memuat cart", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.updateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> binding.rvCartItems.alpha = 0.5f
                is Resource.Success -> {
                    binding.rvCartItems.alpha = 1f
                    updateTotalUI()
                }
                is Resource.Failure -> {
                    binding.rvCartItems.alpha = 1f
                    Toast.makeText(requireContext(), "Gagal update item", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.selectedPaymentMethod.observe(viewLifecycleOwner) { method ->
            updatePaymentMethodUI(method)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnCash.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.CASH) }
        binding.btnTransfer.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.BANK_TRANSFER) }
        binding.btnCard.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.CARD) }
        binding.btnEdc.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.EDC) }
        binding.btnQris.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.QRIS) }

        binding.layoutPay.setOnClickListener {
            onPayClicked()
        }
    }

    private fun onPayClicked() {
        val method = viewModel.selectedPaymentMethod.value ?: PaymentMethod.CASH
        val cartId = arguments?.getString("cartId") ?: return
        val total = viewModel.getCartTotal()
        val items = (viewModel.cartState.value as? Resource.Success)?.value?.shoppingCartItems ?: emptyList()

        when (method) {
            PaymentMethod.CASH -> {
                // Navigate ke CashPayment fragment
                val cashFragment = CashPayment.newInstance(cartId, total)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, cashFragment)
                    .addToBackStack(null)
                    .commit()
            }
            PaymentMethod.BANK_TRANSFER -> {
                // Navigate ke TransferPayment fragment
                val transferFragment = TransferPayment.newInstance(cartId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, transferFragment)
                    .addToBackStack(null)
                    .commit()
            }
            PaymentMethod.EDC -> {
                // Navigate ke EdcPayment fragment
                val edcFragment = EdcPayment.newInstance(cartId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, edcFragment)
                    .addToBackStack(null)
                    .commit()
            }
            PaymentMethod.QRIS -> {
                // Navigate ke QrisPayment fragment
                val qrisFragment = QrisPayment.newInstance(cartId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, qrisFragment)
                    .addToBackStack(null)
                    .commit()
            }
            else -> {
                // CARD → langsung dialog konfirmasi
                dimBackground(true)
                val dialog = DialogPaymentConfirmation.newInstance(
                    cartId = cartId,
                    totalAmount = total,
                    paymentMethod = method,
                    cartItems = items
                )
                dialog.show(childFragmentManager, "dialog_confirmation")
                childFragmentManager.setFragmentResultListener("dismiss_confirmation", viewLifecycleOwner) { _, _ ->
                    dimBackground(false)
                }
            }
        }
    }

    private fun dimBackground(dim: Boolean) {
        val overlay = requireActivity().findViewById<View>(R.id.activityDimOverlay) ?: return
        if (dim) {
            overlay.visibility = View.VISIBLE
            overlay.animate().alpha(1f).setDuration(200).start()
        } else {
            overlay.animate().alpha(0f).setDuration(200).withEndAction {
                overlay.visibility = View.GONE
            }.start()
        }
    }

    private fun updateTotalUI() {
        val total = viewModel.getCartTotal()
        binding.tvTotalAmount.text = "Rp. ${formatCurrency(total)}"
        binding.tvBottomTotal.text = formatCurrency(total)
    }

    private fun updatePaymentMethodUI(method: PaymentMethod) {
        val allButtons = listOf(
            binding.btnCash,
            binding.btnTransfer,
            binding.btnCard,
            binding.btnEdc,
            binding.btnQris
        )

        allButtons.forEach { btn ->
            btn.setBackgroundResource(R.drawable.bg_white_rounded_stroke)
            for (i in 0 until btn.childCount) {
                val child = btn.getChildAt(i)
                if (child is android.widget.TextView) {
                    child.setTextColor(0xFF333333.toInt())
                }
                if (child is android.widget.ImageView) {
                    child.setBackgroundResource(R.drawable.bg_violet_circle)
                    child.setColorFilter(
                        androidx.core.content.ContextCompat.getColor(requireContext(), R.color.mainPurple)
                    )
                }
            }
        }

        val selectedBtn = when (method) {
            PaymentMethod.CASH -> binding.btnCash
            PaymentMethod.BANK_TRANSFER -> binding.btnTransfer
            PaymentMethod.CARD -> binding.btnCard
            PaymentMethod.EDC -> binding.btnEdc
            PaymentMethod.QRIS -> binding.btnQris
        }

        selectedBtn.setBackgroundResource(R.drawable.bg_purple_rounded)
        for (i in 0 until selectedBtn.childCount) {
            val child = selectedBtn.getChildAt(i)
            if (child is android.widget.TextView) {
                child.setTextColor(0xFFFFFFFF.toInt())
            }
            if (child is android.widget.ImageView) {
                child.background = null
                child.setColorFilter(0xFFFFFFFF.toInt())
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvCartItems.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}