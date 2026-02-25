package id.co.psplauncher.ui.fragments.dialog_paymentconfirmation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import id.co.psplauncher.databinding.FragmentCartBinding
import id.co.psplauncher.databinding.FragmentDialogPaymentConfirmationBinding
import id.co.psplauncher.ui.fragments.fragment_cart.FragmentCartViewModel

class DialogPaymentConfirmation : DialogFragment() {
    private lateinit var binding: FragmentDialogPaymentConfirmationBinding
    private val dialogPaymentConfirmationViewModel: DialogPaymentConfirmationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDialog()!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentDialogPaymentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogPaymentConfirmationViewModel.Close(this, binding)
    }
}