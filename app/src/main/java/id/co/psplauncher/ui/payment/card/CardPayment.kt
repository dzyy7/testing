package id.co.psplauncher.ui.payment.card

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.response.CardInquiryResponse
import id.co.psplauncher.databinding.FragmentCardPaymentBinding
import id.co.psplauncher.ui.fragments.detail_invoice.DetailInvoiceFragment
import id.co.psplauncher.ui.fragments.dialog_detailtransaction.DialogDetailTransaction

@AndroidEntryPoint
class CardPayment : Fragment() {

    private var _binding: FragmentCardPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CardPaymentViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var cartId: String = ""

    companion object {
        fun newInstance(cartId: String) = CardPayment().apply {
            arguments = Bundle().apply {
                putString("cartId", cartId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartId = arguments?.getString("cartId") ?: run {
            Toast.makeText(requireContext(), "Cart ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        setupNfc()
        setupObservers()
        setupClickListeners()
        viewModel.loadCart(cartId)

        showTapCardState()
    }

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        if (nfcAdapter == null || !nfcAdapter!!.isEnabled) {
            // Emulator / NFC tidak tersedia → fallback ke input manual
            showDebugNfcDialog()
            return
        }

        val intent = Intent(requireActivity(), requireActivity().javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun showDebugNfcDialog() {
        val editText = android.widget.EditText(requireContext()).apply {
            hint = "Contoh: 0303184267"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(48, 32, 48, 32)
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Simulasi Tap Kartu")
            .setMessage("Masukkan NFC ID kartu:")
            .setView(editText)
            .setPositiveButton("Cari Kartu") { _, _ ->
                val nfcId = editText.text.toString().trim()
                if (nfcId.isNotEmpty()) {
                    viewModel.onNfcTagRead(nfcId)
                } else {
                    Toast.makeText(requireContext(), "NFC ID tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    private fun enableNfcForegroundDispatch() {
        val adapter = nfcAdapter ?: return
        if (!adapter.isEnabled) return
        try {
            val filters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            )
            adapter.enableForegroundDispatch(
                requireActivity(),
                pendingIntent,
                filters,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(requireActivity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onNfcIntent(intent: Intent) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.let {
            val tagId = bytesToHexDecimal(it.id)
            viewModel.onNfcTagRead(tagId)
        }
    }

    private fun bytesToHexDecimal(bytes: ByteArray): String {
        val reversed = bytes.reversedArray()
        var result = 0L
        for (b in reversed) {
            result = result * 256 + (b.toLong() and 0xFF)
        }
        // Pad dengan leading zero sampai 10 digit (sesuai format NFC reader external)
        return result.toString().padStart(10, '0')
    }

    private fun setupObservers() {
        viewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Success -> {
                    binding.valGrandTotal.text = "Rp. ${formatCurrency(viewModel.getCartTotal())}"
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Gagal memuat data transaksi", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.inquiryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                null -> showTapCardState()
                is Resource.Loading -> showLoadingState()
                is Resource.Success -> showCardDetailState(state.value)
                is Resource.Failure -> {
                    showTapCardState()
                    val errorMessage = state.errorBody?.string() ?: "Kartu tidak dikenali, coba lagi"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.transactionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.btnConfirm.isEnabled = false
                    binding.btnConfirm.text = "Memproses..."
                }
                is Resource.Success -> {
                    CartManager.clearCart()
                    navigateToInvoice(state.value)
                }
                is Resource.Failure -> {
                    binding.btnConfirm.isEnabled = true
                    binding.btnConfirm.text = "Konfirmasi Pembayaran"
                    val errorMessage = state.errorBody?.string() ?: "Transaksi gagal, coba lagi"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.cardDetail.setOnClickListener {
            showDetailTransaction()
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.submitTransaction()
        }

        binding.btnScanAgain.setOnClickListener {
            viewModel.resetInquiry()
            if (nfcAdapter == null || nfcAdapter?.isEnabled == false) {
                showDebugNfcDialog()
            }
        }
    }

    // ─── State Management ────────────────────────────────────────────────

    private fun showTapCardState() {
        binding.layoutTapCard.visibility = View.VISIBLE
        binding.layoutCardDetail.visibility = View.GONE
        binding.btnConfirm.visibility = View.GONE
        binding.btnScanAgain.visibility = View.GONE
        binding.progressInquiry.visibility = View.GONE
    }

    private fun showLoadingState() {
        binding.layoutTapCard.visibility = View.GONE
        binding.layoutCardDetail.visibility = View.GONE
        binding.btnConfirm.visibility = View.GONE
        binding.btnScanAgain.visibility = View.GONE
        binding.progressInquiry.visibility = View.VISIBLE
    }

    private fun showCardDetailState(card: CardInquiryResponse) {
        binding.progressInquiry.visibility = View.GONE
        binding.layoutTapCard.visibility = View.GONE
        binding.layoutCardDetail.visibility = View.VISIBLE
        binding.btnConfirm.visibility = View.VISIBLE
        binding.btnScanAgain.visibility = View.VISIBLE

        binding.tvCardHolderName.text = card.name
        binding.tvCardNumber.text = formatNfcId(card.nfcId)
        binding.tvSaldo.text = "Rp. ${formatCurrency(card.balance)}"

        if (card.photoUrl.isNotEmpty() && card.photoUrl != "-") {
            Glide.with(requireContext())
                .load(card.photoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(binding.imgCardHolder)
        } else {
            binding.imgCardHolder.setImageResource(R.drawable.ic_launcher_background)
        }

        val total = viewModel.getCartTotal()
        val hasSufficientBalance = card.isUnlimited || card.balance >= total
        binding.btnConfirm.isEnabled = hasSufficientBalance
        binding.btnConfirm.text = if (!hasSufficientBalance) "Saldo Tidak Cukup" else "Konfirmasi Pembayaran"
        if (!hasSufficientBalance) {
            Toast.makeText(requireContext(), "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatNfcId(nfcId: String): String {
        // Display as groups of 4 characters for readability
        return nfcId.chunked(4).joinToString(" ")
    }

    private fun showDetailTransaction() {
        val dialog = DialogDetailTransaction.newInstance(
            cartItems = viewModel.getCartItems(),
            totalAmount = viewModel.getCartTotal()
        )
        dialog.show(childFragmentManager, "DialogDetailTransaction")
    }

    private fun navigateToInvoice(response: id.co.psplauncher.data.network.response.TransactionResponse) {
        val fragment = DetailInvoiceFragment.newInstance(response = response)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}