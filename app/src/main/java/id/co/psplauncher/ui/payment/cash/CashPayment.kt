package id.co.psplauncher.ui.payment.cash

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import id.co.psplauncher.databinding.FragmentCashPaymentBinding

class CashPayment : Fragment() {
    private lateinit var binding: FragmentCashPaymentBinding
    private val viewModel: CashPaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }
}