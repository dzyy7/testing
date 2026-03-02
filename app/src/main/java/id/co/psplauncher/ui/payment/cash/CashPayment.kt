package id.co.psplauncher.ui.payment.cash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.databinding.FragmentCashPaymentBinding
import id.co.psplauncher.ui.fragments.dialog_paymentconfirmation.DialogPaymentConfirmation
import id.co.psplauncher.ui.payment.PaymentMethod

@AndroidEntryPoint
class CashPayment : Fragment() {
    private var _binding: FragmentCashPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CashPaymentViewModel by viewModels()

    private var cartId: String = ""
    private var totalAmount: Double = 0.0

    companion object {
        fun newInstance(cartId: String, totalAmount: Double) = CashPayment().apply {
            arguments = Bundle().apply {
                putString("cartId", cartId)
                putDouble("totalAmount", totalAmount)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCashPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartId = arguments?.getString("cartId") ?: ""
        totalAmount = arguments?.getDouble("totalAmount") ?: 0.0

        viewModel.setTotalAmount(totalAmount)

        // Tampilkan total di displayAmount awal
        binding.displayAmount.text = formatCurrency(totalAmount)
        binding.valTotal.text = "Rp. ${formatCurrency(totalAmount)}"

        setupObservers()
        setupKeypad()
        setupChips()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.inputAmount.observe(viewLifecycleOwner) { amount ->
            val value = amount.toDoubleOrNull() ?: 0.0
            binding.displayAmount.text = formatCurrency(value)
        }

        viewModel.kembalian.observe(viewLifecycleOwner) { kembalian ->
            binding.valKembalian.text = "Rp. ${formatCurrency(kembalian)}"
        }
    }

    private fun setupKeypad() {
        val keyMap = mapOf(
            binding.key0 to "0",
            binding.key1 to "1",
            binding.key2 to "2",
            binding.key3 to "3",
            binding.key4 to "4",
            binding.key5 to "5",
            binding.key6 to "6",
            binding.key7 to "7",
            binding.key8 to "8",
            binding.key9 to "9",
            binding.keyDot to ".",
            binding.keyClear to "C"
        )
        keyMap.forEach { (btn, key) ->
            btn.setOnClickListener { viewModel.onKeyPress(key) }
        }
    }

    private fun setupChips() {
        mapOf(
            binding.btn100 to 100.0,
            binding.btn200 to 200.0,
            binding.btn500 to 500.0,
            binding.btn1000 to 1000.0,
            binding.btn2000 to 2000.0,
            binding.btn5000 to 5000.0,
            binding.btn10000 to 10000.0,
            binding.btn20000 to 20000.0,
            binding.btn50000 to 50000.0,
            binding.btn100000 to 100000.0
        ).forEach { (btn, amount) ->
            btn.setOnClickListener { viewModel.onChipClick(amount) }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            if (!viewModel.isValid()) {
                Toast.makeText(requireContext(), "Nominal pembayaran kurang dari total", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dimActivityBackground(true)

            val dialog = DialogPaymentConfirmation.newInstance(
                cartId = cartId,
                totalAmount = totalAmount,
                nominalBayar = viewModel.getNominalBayar(),
                paymentMethod = PaymentMethod.CASH
            )
            dialog.show(parentFragmentManager, "dialog_confirmation")

            parentFragmentManager.setFragmentResultListener("dismiss_confirmation", viewLifecycleOwner) { _, _ ->
                dimActivityBackground(false)
            }
        }
    }

    private fun dimActivityBackground(dim: Boolean) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}