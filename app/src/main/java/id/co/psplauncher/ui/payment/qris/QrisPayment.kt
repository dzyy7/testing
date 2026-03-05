package id.co.psplauncher.ui.payment.qris

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentQrisPaymentBinding
import id.co.psplauncher.ui.fragments.dialog_detailtransaction.DialogDetailTransaction
import id.co.psplauncher.ui.fragments.dialog_paymentconfirmation.DialogPaymentConfirmation
import id.co.psplauncher.ui.payment.PaymentMethod

@AndroidEntryPoint
class QrisPayment : Fragment() {

    private var _binding: FragmentQrisPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QrisPaymentViewModel by viewModels()

    private var cartId: String = ""

    companion object {
        fun newInstance(cartId: String) = QrisPayment().apply {
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
        _binding = FragmentQrisPaymentBinding.inflate(inflater, container, false)
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

        viewModel.isFullscreen.observe(viewLifecycleOwner) { isFullscreen ->
            if (isFullscreen) {
                showFullscreenQr()
            } else {
                hideFullscreenQr()
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

        binding.btnFullscreen.setOnClickListener {
            viewModel.toggleFullscreen()
        }

        binding.cardDetail.setOnClickListener {
            showDetailTransaction()
        }

        binding.btnConfirm.setOnClickListener {
            showPaymentConfirmation()
        }

        // Close fullscreen when clicking outside (on the expanded view)
        binding.root.setOnClickListener {
            if (viewModel.isFullscreen.value == true) {
                viewModel.setFullscreen(false)
            }
        }
    }

    private fun showFullscreenQr() {
        val container = binding.fullscreenContainer
        container.removeAllViews()

        val fullscreenImage = ImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setImageDrawable(binding.imgQrCode.drawable)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setPadding(40, 40, 40, 40)
        }

        container.addView(fullscreenImage)
        container.visibility = View.VISIBLE

        container.setOnClickListener {
            viewModel.setFullscreen(false)
        }
    }

    private fun hideFullscreenQr() {
        binding.fullscreenContainer.visibility = View.GONE
    }

    private fun showDetailTransaction() {
        dimBackground(true)
        val dialog = DialogDetailTransaction.newInstance(
            cartItems = viewModel.getCartItems(),
            totalAmount = viewModel.getCartTotal()
        )
        dialog.show(childFragmentManager, "DialogDetailTransaction")
        childFragmentManager.setFragmentResultListener("detail_transaction_dismiss", viewLifecycleOwner) { _, _ ->
            dimBackground(false)
        }
    }

    private fun showPaymentConfirmation() {
        dimBackground(true)
        val dialog = DialogPaymentConfirmation.newInstance(
            cartId = cartId,
            totalAmount = viewModel.getCartTotal(),
            paymentMethod = PaymentMethod.QRIS,
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
            ObjectAnimator.ofFloat(overlay, "alpha", 0f, 1f).apply {
                duration = 200
                start()
            }
        } else {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
