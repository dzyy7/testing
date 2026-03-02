package id.co.psplauncher.ui.fragments.detail_invoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.response.OrderDetail
import id.co.psplauncher.databinding.ItemConfirmationProductBinding

class InvoiceProductAdapter(
    private val items: List<OrderDetail>
) : RecyclerView.Adapter<InvoiceProductAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemConfirmationProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderDetail) {
            val price = item.price
            val qty = item.quantity
            binding.textProductName.text = item.productName
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