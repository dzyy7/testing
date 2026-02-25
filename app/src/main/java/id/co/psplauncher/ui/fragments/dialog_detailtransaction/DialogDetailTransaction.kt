package id.co.psplauncher.ui.fragments.dialog_detailtransaction

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.co.psplauncher.databinding.FragmentDialogDetailTransactionBinding
import id.co.psplauncher.databinding.FragmentDialogPaymentConfirmationBinding
import id.co.psplauncher.ui.fragments.dialog_paymentconfirmation.DialogPaymentConfirmationViewModel

class DialogDetailTransaction : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDialogDetailTransactionBinding
    private val dialogDetailTransactionViewModel: DialogDetailTransactionViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDialog()!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentDialogDetailTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogDetailTransactionViewModel.Close(this, binding)
    }
}