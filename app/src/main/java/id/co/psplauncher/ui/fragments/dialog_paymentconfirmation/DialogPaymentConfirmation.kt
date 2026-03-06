package id.co.psplauncher.ui.fragments.dialog_paymentconfirmation

import android.animation.ObjectAnimator
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
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.data.network.response.TransactionResponse
import id.co.psplauncher.data.network.transaction.showKembalian
import id.co.psplauncher.databinding.FragmentDialogPaymentConfirmationBinding
import id.co.psplauncher.ui.fragments.detail_invoice.DetailInvoiceFragment
import id.co.psplauncher.ui.payment.PaymentMethod

@AndroidEntryPoint
class DialogPaymentConfirmation : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogPaymentConfirmationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DialogPaymentConfirmationViewModel by viewModels()

    private var cartId: String = ""
    private var totalAmount: Double = 0.0
    private var nominalBayar: Double = 0.0
    private var paymentMethod: PaymentMethod = PaymentMethod.CASH
    private var cartItems: List<ShoppingCartItemResponse> = emptyList()

    companion object {
        fun newInstance(
            cartId: String,
            totalAmount: Double,
            nominalBayar: Double = 0.0,
            paymentMethod: PaymentMethod,
            cartItems: List<ShoppingCartItemResponse> = emptyList()
        ) = DialogPaymentConfirmation().apply {
            arguments = Bundle().apply {
                putString("cartId", cartId)
                putDouble("totalAmount", totalAmount)
                putDouble("nominalBayar", nominalBayar)
                putSerializable("paymentMethod", paymentMethod)
            }
            this.cartItems = cartItems
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogPaymentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setDimAmount(0.6f)
            addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartId = arguments?.getString("cartId") ?: ""
        totalAmount = arguments?.getDouble("totalAmount") ?: 0.0
        nominalBayar = arguments?.getDouble("nominalBayar") ?: 0.0
        paymentMethod = (arguments?.getSerializable("paymentMethod") as? PaymentMethod) ?: PaymentMethod.CASH

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        // Hitung subtotal, tax, dan total dengan tax
        val subtotal = cartItems.sumOf { it.product.sellingPrice * it.quantity }
        val totalTax = cartItems.sumOf { it.product.tax * it.product.sellingPrice * it.quantity / 100 }
        val totalWithTax = subtotal + totalTax

        // Tampilkan subtotal, tax, dan total
        binding.valueSubtotal.text = "Rp. ${formatCurrency(subtotal)}"
        binding.valueTax.text = "Rp. ${formatCurrency(totalTax)}"
        binding.valueTotalAmount.text = "Rp. ${formatCurrency(totalWithTax)}"

        // Total bayar: untuk CASH pakai nominalBayar, untuk lainnya pakai totalWithTax
        val totalBayar = if (paymentMethod.showKembalian()) nominalBayar else totalWithTax
        binding.valueTotalBayar.text = "Rp. ${formatCurrency(totalBayar)}"

        // Kembalian hanya untuk CASH
        if (paymentMethod.showKembalian()) {
            val kembalian = maxOf(0.0, nominalBayar - totalWithTax)
            binding.labelKembalian.visibility = View.VISIBLE
            binding.valueKembalian.visibility = View.VISIBLE
            binding.valueKembalian.text = "Rp. ${formatCurrency(kembalian)}"
        } else {
            binding.labelKembalian.visibility = View.GONE
            binding.valueKembalian.visibility = View.GONE
        }

        binding.iconHeader.setImageResource(
            when (paymentMethod) {
                PaymentMethod.CASH -> R.drawable.ic_cash
                PaymentMethod.QRIS -> R.drawable.ic_qris
                else -> R.drawable.ic_transferbank
            }
        )

        binding.textDescription.text = "Apakah anda yakin akan melakukan pembayaran via ${paymentMethod.name.replace("_", " ")}?"
    }

    private fun setupRecyclerView() {
        val adapter = ConfirmationProductAdapter(cartItems)
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.transactionResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.btnPay.isEnabled = false
                    binding.btnPay.text = "Memproses..."
                }
                is Resource.Success -> {
                    dismiss()
                    navigateToInvoice(state.value)
                }
                is Resource.Failure -> {
                    binding.btnPay.isEnabled = true
                    binding.btnPay.text = "Pay"
                    Toast.makeText(requireContext(), "Transaksi gagal, coba lagi", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnPay.setOnClickListener {
            viewModel.submitTransaction(cartId, paymentMethod)
        }
    }

    private fun navigateToInvoice(response: TransactionResponse) {
        // Hide overlay otomatis sebelum navigate ke invoice
        hideDimOverlay()
        
        CartManager.clearCart()
        val fragment = DetailInvoiceFragment.newInstance(
            response = response,
            nominalBayar = nominalBayar
        )
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun hideDimOverlay() {
        val overlay = requireActivity().findViewById<View>(R.id.activityDimOverlay) ?: return
        if (overlay.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat(overlay, "alpha", 1f, 0f).apply {
                duration = 200
                start()
            }.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    overlay.visibility = View.GONE
                }
            })
        }
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("dismiss_confirmation", Bundle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}