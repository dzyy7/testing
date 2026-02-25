package id.co.psplauncher.ui.payment.edc

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import id.co.psplauncher.databinding.FragmentEdcPaymentBinding

class EdcPayment : Fragment() {

    private lateinit var binding: FragmentEdcPaymentBinding
    private val viewModel: EdcPaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

}