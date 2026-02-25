package id.co.psplauncher.ui.fragments.dialog_paymentconfirmation

import androidx.lifecycle.ViewModel
import id.co.psplauncher.databinding.FragmentCartBinding
import id.co.psplauncher.databinding.FragmentDialogPaymentConfirmationBinding


class DialogPaymentConfirmationViewModel : ViewModel() {
    fun Close(appCompatActivity: DialogPaymentConfirmation, binding: FragmentDialogPaymentConfirmationBinding){
        binding.btnCancel.setOnClickListener {
            appCompatActivity.dismiss()
        }
    }
}