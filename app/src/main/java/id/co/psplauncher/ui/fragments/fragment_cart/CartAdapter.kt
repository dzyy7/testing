package id.co.psplauncher.ui.fragments.fragment_cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.local.CartItem
import id.co.psplauncher.databinding.ItemCartProductBinding

class CartAdapter(
    private var items: List<CartItem>,
    private val onQuantityChanged: (productId: String, newQuantity: Int) -> Unit,
    private val onItemRemoved: (productId: String) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.tvProductName.text = item.product.name
            binding.tvProductPrice.text = "Rp. ${formatCurrency(item.product.sellingPrice)}"
            binding.tvQuantity.text = item.quantity.toString()

            val imageUrl = item.product.images.firstOrNull()?.thumbnail
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.ic_launcher_background)
            }

            binding.btnPlus.setOnClickListener {
                val newQty = item.quantity + 1
                if (newQty <= item.product.stock) {
                    onQuantityChanged(item.product.id, newQty)
                }
            }

            binding.btnMinus.setOnClickListener {
                val newQty = item.quantity - 1
                if (newQty <= 0) {
                    onItemRemoved(item.product.id)
                } else {
                    onQuantityChanged(item.product.id, newQty)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
