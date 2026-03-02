package id.co.psplauncher.ui.fragments.dialog_paymentconfirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.databinding.ItemConfirmationProductBinding

class ConfirmationProductAdapter(
    private val items: List<ShoppingCartItemResponse>
) : RecyclerView.Adapter<ConfirmationProductAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemConfirmationProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingCartItemResponse) {
            val price = item.product.sellingPrice
            val qty = item.quantity
            binding.textProductName.text = item.product.name
            binding.textProductDetail.text = "$qty x Rp. ${formatCurrency(price)}"
            binding.textProductTotal.text = "Rp. ${formatCurrency(price * qty)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemConfirmationProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}