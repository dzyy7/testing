package id.co.psplauncher.ui.fragments.dialog_detailtransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.databinding.ItemTransactionRowBinding

class DetailTransactionAdapter(
    private val items: List<ShoppingCartItemResponse>
) : RecyclerView.Adapter<DetailTransactionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTransactionRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingCartItemResponse) {
            binding.tvProductName.text = item.product.name
            binding.tvQty.text = "${item.quantity}x"
            binding.tvSinglePrice.text = "Rp. ${formatCurrency(item.product.sellingPrice)}"
            binding.tvTotalPrice.text = "Rp. ${formatCurrency(item.product.sellingPrice * item.quantity)}"

            val imageUrl = item.product.images.firstOrNull()
            Glide.with(binding.imgProduct.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imgProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemTransactionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}