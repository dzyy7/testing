package id.co.psplauncher.ui.payment.cash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CashPaymentViewModel @Inject constructor() : ViewModel() {

    private val _inputAmount = MutableLiveData("0")
    val inputAmount: LiveData<String> = _inputAmount

    private val _kembalian = MutableLiveData(0.0)
    val kembalian: LiveData<Double> = _kembalian

    private var totalAmount: Double = 0.0

    fun setTotalAmount(amount: Double) {
        totalAmount = amount
        recalculate()
    }

    fun onKeyPress(key: String) {
        val current = _inputAmount.value ?: "0"
        val newValue = when {
            key == "C" -> "0"
            key == "." && current.contains(".") -> current // jangan duplikat titik
            current == "0" && key != "." -> key
            else -> current + key
        }
        _inputAmount.value = newValue
        recalculate()
    }

    fun onChipClick(amount: Double) {
        val current = (_inputAmount.value?.toDoubleOrNull() ?: 0.0) + amount
        _inputAmount.value = formatNumber(current)
        recalculate()
    }

    private fun recalculate() {
        val paid = _inputAmount.value?.toDoubleOrNull() ?: 0.0
        _kembalian.value = maxOf(0.0, paid - totalAmount)
    }

    fun getNominalBayar(): Double = _inputAmount.value?.toDoubleOrNull() ?: 0.0

    fun isValid(): Boolean = getNominalBayar() >= totalAmount

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}