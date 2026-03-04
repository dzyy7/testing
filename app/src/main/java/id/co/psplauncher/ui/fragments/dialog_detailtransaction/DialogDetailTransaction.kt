package id.co.psplauncher.ui.fragments.dialog_detailtransaction

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.databinding.FragmentDialogDetailTransactionBinding

class DialogDetailTransaction : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogDetailTransactionBinding? = null
    private val binding get() = _binding!!

    private var cartItems: List<ShoppingCartItemResponse> = emptyList()
    private var totalAmount: Double = 0.0

    companion object {
        fun newInstance(
            cartItems: List<ShoppingCartItemResponse>,
            totalAmount: Double
        ) = DialogDetailTransaction().apply {
            this.cartItems = cartItems
            this.totalAmount = totalAmount
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentDialogDetailTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        bindSummary()

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        val adapter = DetailTransactionAdapter(cartItems)
        binding.rvTransactionItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun bindSummary() {
        val totalTax = cartItems.sumOf {
            it.product.tax / 100.0 * it.product.sellingPrice * it.quantity
        }
        val netPrice = totalAmount - totalTax

        binding.tvNetPrice.text = "Rp. ${formatCurrency(netPrice)}"
        binding.tvTax.text = "Rp. ${formatCurrency(totalTax)}"
        binding.tvTotalAmount.text = "Rp. ${formatCurrency(totalAmount)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}