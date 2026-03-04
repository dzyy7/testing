package id.co.psplauncher.ui.payment.edc

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
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentEdcPaymentBinding
import id.co.psplauncher.ui.fragments.dialog_detailtransaction.DialogDetailTransaction
import id.co.psplauncher.ui.fragments.dialog_paymentconfirmation.DialogPaymentConfirmation
import id.co.psplauncher.ui.payment.PaymentMethod

@AndroidEntryPoint
class EdcPayment : Fragment() {

    private var _binding: FragmentEdcPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EdcPaymentViewModel by viewModels()

    private var cartId: String = ""

    companion object {
        fun newInstance(cartId: String) = EdcPayment().apply {
            arguments = Bundle().apply {
                putString("cartId", cartId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEdcPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartId = arguments?.getString("cartId") ?: run {
            Toast.makeText(requireContext(), "Cart ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        setupObservers()
        setupClickListeners()
        viewModel.loadCart(cartId)
    }

    private fun setupObservers() {
        viewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.btnConfirm.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnConfirm.isEnabled = true
                    binding.valGrandTotal.text = "Rp. ${formatCurrency(viewModel.getCartTotal())}"
                }
                is Resource.Failure -> {
                    binding.btnConfirm.isEnabled = false
                    Toast.makeText(requireContext(), "Gagal memuat data transaksi", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.cardDetail.setOnClickListener {
            showDetailTransaction()
        }

        binding.btnConfirm.setOnClickListener {
            showPaymentConfirmation()
        }
    }

    private fun showDetailTransaction() {
        val dialog = DialogDetailTransaction.newInstance(
            cartItems = viewModel.getCartItems(),
            totalAmount = viewModel.getCartTotal()
        )
        dialog.show(childFragmentManager, "DialogDetailTransaction")
    }

    private fun showPaymentConfirmation() {
        dimBackground(true)
        val dialog = DialogPaymentConfirmation.newInstance(
            cartId = cartId,
            totalAmount = viewModel.getCartTotal(),
            paymentMethod = PaymentMethod.EDC,
            cartItems = viewModel.getCartItems()
        )
        dialog.show(childFragmentManager, "dialog_confirmation")
        childFragmentManager.setFragmentResultListener("dismiss_confirmation", viewLifecycleOwner) { _, _ ->
            dimBackground(false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}