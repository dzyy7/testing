package id.co.psplauncher.ui.payment.qris

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import id.co.psplauncher.databinding.FragmentQrisPaymentBinding

class QrisPayment : Fragment() {
    private lateinit var binding: FragmentQrisPaymentBinding
    private val viewModel: QrisPaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }
}