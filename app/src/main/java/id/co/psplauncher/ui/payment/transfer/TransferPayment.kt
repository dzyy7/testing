package id.co.psplauncher.ui.payment.transfer

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment

import id.co.psplauncher.databinding.FragmentTransferPaymentBinding

class TransferPayment : Fragment() {
    private lateinit var binding: FragmentTransferPaymentBinding
    private val viewModel: TransferPaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

}