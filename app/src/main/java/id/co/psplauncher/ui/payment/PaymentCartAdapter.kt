package id.co.psplauncher.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.databinding.ItemCartPaymentMethodBinding

class PaymentCartAdapter(
    private var items: List<ShoppingCartItemResponse>,
    private val context: Context,
    private val onQuantityChanged: (productId: String, newQuantity: Int) -> Unit
) : RecyclerView.Adapter<PaymentCartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCartPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingCartItemResponse) {
            binding.textProductName.text = item.product.name
            binding.textPrice.text = "Rp. ${formatCurrency(item.product.sellingPrice)}"
            binding.textStock.text = "Total: Rp. ${formatCurrency(item.product.sellingPrice * item.quantity)}"
            binding.textQty.text = item.quantity.toString()
            binding.textTotalPrice.text = "Rp. ${formatCurrency(item.product.sellingPrice * item.quantity)}"

            val imageUrl = item.product.images.firstOrNull()
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.ic_launcher_background)
            }

            binding.btnPlus.setOnClickListener {
                onQuantityChanged(item.product.id, item.quantity + 1)
            }

            binding.btnMinus.setOnClickListener {
                onQuantityChanged(item.product.id, item.quantity - 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ShoppingCartItemResponse>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) =
                items[o].product.id == newItems[n].product.id
            override fun areContentsTheSame(o: Int, n: Int) =
                items[o].quantity == newItems[n].quantity
        })
        items = newItems
        diff.dispatchUpdatesTo(this)
    }
}