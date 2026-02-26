package id.co.psplauncher.ui.fragments.fragment_cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.local.CartItem
import id.co.psplauncher.data.local.CartManager
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

            val imageUrl = item.product.images.firstOrNull()?.thumbnail
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.ic_launcher_background)
            }

            refreshQuantity(item)
        }

        fun refreshQuantity(item: CartItem) {
            // Selalu ambil quantity terbaru dari CartManager — bukan dari item yang bisa stale
            val currentQty = CartManager.getItemQuantity(item.product.id)
            binding.tvQuantity.text = currentQty.toString()

            binding.btnPlus.setOnClickListener {
                // Query fresh saat diklik — bukan nilai yang di-capture saat bind()
                val freshQty = CartManager.getItemQuantity(item.product.id)
                val newQty = freshQty + 1
                if (newQty <= item.product.stock) {
                    onQuantityChanged(item.product.id, newQty)
                }
            }

            binding.btnMinus.setOnClickListener {
                val freshQty = CartManager.getItemQuantity(item.product.id)
                val newQty = freshQty - 1
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
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == PAYLOAD_QUANTITY_CHANGED) {
            // Partial update — hanya refresh qty & listener, tidak reload gambar/nama
            holder.refreshQuantity(items[position])
        } else {
            holder.bind(items[position])
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CartItem>) {
        val diffResult = DiffUtil.calculateDiff(CartDiffCallback(items, newItems))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class CartDiffCallback(
        private val oldList: List<CartItem>,
        private val newList: List<CartItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos].product.id == newList[newPos].product.id

        override fun areContentsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos].quantity == newList[newPos].quantity

        override fun getChangePayload(oldPos: Int, newPos: Int) = PAYLOAD_QUANTITY_CHANGED
    }

    companion object {
        private const val PAYLOAD_QUANTITY_CHANGED = "quantity_changed"
    }
}