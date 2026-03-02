package id.co.psplauncher.ui.fragments.detail_invoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.databinding.FragmentDetailInvoiceBinding
import id.co.psplauncher.data.network.response.TransactionResponse

class DetailInvoiceFragment : Fragment() {
    private var _binding: FragmentDetailInvoiceBinding? = null
    private val binding get() = _binding!!

    private var transactionResponse: TransactionResponse? = null

    companion object {
        fun newInstance(response: TransactionResponse) = DetailInvoiceFragment().apply {
            this.transactionResponse = response
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailInvoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionResponse?.let { bindData(it) }

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.buttonCloseTransaction.setOnClickListener {
            // Pop semua back stack kembali ke POS
            requireActivity().supportFragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        binding.buttonPrintTransaction.setOnClickListener {
            // TODO: implement print
        }
    }

    private fun bindData(response: TransactionResponse) {
        binding.textLabelShopName.text = response.merchantName
        binding.textLabelCashierName.text = "Kasir: ${response.cashierName}"
        binding.textLabelDateTransaction.text = response.transactionTime
        binding.textLabelSuccessMessage.text = if (response.success) "Pembayaran anda telah berhasil" else response.message
        binding.valueIdTransaction.text = response.transactionId
        binding.valuePaymentMethod.text = response.paymentMethod.replace("_", " ")
        binding.valueNetPrice.text = "Rp. ${formatCurrency(response.totalAmount)}"
        binding.valueTax.text = "Rp. ${formatCurrency(response.orderDetails.sumOf { it.tax })}"
        binding.valueTotalAmount.text = "Rp. ${formatCurrency(response.totalAmount)}"
        binding.valueTotalPay.text = "Rp. ${formatCurrency(response.totalAmount)}"
        binding.valueChange.text = "Rp. 0"

        // RecyclerView produk
        val adapter = InvoiceProductAdapter(response.orderDetails)
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}