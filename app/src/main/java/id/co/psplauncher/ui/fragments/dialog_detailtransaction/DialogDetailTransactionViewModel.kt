package id.co.psplauncher.ui.fragments.dialog_detailtransaction

import androidx.lifecycle.ViewModel
import id.co.psplauncher.databinding.FragmentDialogDetailTransactionBinding
import id.co.psplauncher.databinding.FragmentDialogPaymentConfirmationBinding
import id.co.psplauncher.ui.fragments.dialog_paymentconfirmation.DialogPaymentConfirmation

class DialogDetailTransactionViewModel : ViewModel() {
    fun Close(appCompatActivity: DialogDetailTransaction, binding: FragmentDialogDetailTransactionBinding){
        binding.btnClose.setOnClickListener {
            appCompatActivity.dismiss()
        }
    }
}