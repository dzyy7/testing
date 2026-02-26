package id.co.psplauncher.ui.pos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.response.Product
import id.co.psplauncher.databinding.ItemProductGridBinding
import id.co.psplauncher.databinding.ItemProductListBinding

class GridProductAdapter(
    private var items: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<GridProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product) {
            binding.textProductName.text = item.name
            binding.textStock.text = "Stock: ${item.stock}"
            binding.textPrice.text = "Rp. ${formatCurrency(item.sellingPrice)}"
            Glide.with(context)
                .load(item.images.firstOrNull()?.thumbnail ?: R.drawable.ic_launcher_background)
                .into(binding.imgProduct)
            refreshBadge(item)
        }

        fun refreshBadge(item: Product) {
            val cartQty = CartManager.getItemQuantity(item.id)
            binding.textBadge.text = cartQty.toString()
            if (cartQty > 0) {
                binding.textBadge.visibility = View.VISIBLE
                binding.imageBadge.visibility = View.VISIBLE
            } else {
                binding.textBadge.visibility = View.GONE
                binding.imageBadge.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        if (item.stock != 0) {
            holder.itemView.setOnClickListener { onProductClick(item) }
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.contains(PAYLOAD_CART_CHANGED)) {
            holder.refreshBadge(items[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun updateData(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }

    fun refreshCartBadges() {
        notifyItemRangeChanged(0, itemCount, PAYLOAD_CART_CHANGED)
    }

    override fun getItemCount() = items.size

    companion object {
        private const val PAYLOAD_CART_CHANGED = "cart_changed"
    }
}


class ListProductAdapter(
    private var items: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<ListProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product) {
            binding.textProductName.text = item.name
            binding.textStock.text = "Stock: ${item.stock}"
            binding.textPrice.text = "Rp. ${formatCurrency(item.sellingPrice)}"
            Glide.with(context)
                .load(item.images.firstOrNull()?.thumbnail ?: R.drawable.ic_launcher_background)
                .into(binding.imgProduct)

            refreshQty(item)
            attachListeners(item)
        }

        fun refreshQty(item: Product) {
            // Selalu query CartManager langsung — bukan capture dari item
            binding.textQty.text = CartManager.getItemQuantity(item.id).toString()
        }

        fun attachListeners(item: Product) {
            if (item.stock == 0) return

            binding.btnPlus.setOnClickListener {
                // Query fresh saat diklik
                val freshQty = CartManager.getItemQuantity(item.id)
                if (freshQty < item.stock) {
                    CartManager.addToCart(item, 1)
                } else {
                    Toast.makeText(context, "Stock limit reached", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnMinus.setOnClickListener {
                val freshQty = CartManager.getItemQuantity(item.id)
                if (freshQty > 0) {
                    CartManager.updateQuantity(item.id, freshQty - 1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.contains(PAYLOAD_CART_CHANGED)) {
            holder.refreshQty(items[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun updateData(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }

    fun refreshCartQtys() {
        notifyItemRangeChanged(0, itemCount, PAYLOAD_CART_CHANGED)
    }

    override fun getItemCount() = items.size

    companion object {
        private const val PAYLOAD_CART_CHANGED = "cart_changed"
    }
}