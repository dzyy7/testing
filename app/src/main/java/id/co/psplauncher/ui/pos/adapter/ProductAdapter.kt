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
): RecyclerView.Adapter<GridProductAdapter.ProductViewHolder>() {
    inner class ProductViewHolder(val binding: ItemProductGridBinding):
            RecyclerView.ViewHolder(binding.root){
            fun bind(item: Product) {
                binding.textProductName.text = item.name
                binding.textStock.text = "Stock: ${item.stock}"
                binding.textPrice.text = "Rp. ${formatCurrency(item.sellingPrice)}"
                Glide.with(context).load(item.images.firstOrNull()?.thumbnail ?: R.drawable.ic_launcher_background).into(binding.imgProduct)
                val cartQty = CartManager.getItemQuantity(item.id)
                binding.textBadge.text = cartQty.toString()
                if (cartQty > 0){
                    binding.textBadge.visibility = View.VISIBLE
                    binding.imageBadge.visibility = View.VISIBLE
                }else{
                    binding.textBadge.visibility = View.GONE
                    binding.imageBadge.visibility = View.GONE
                }
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        if(item.stock != 0){
            holder.itemView.setOnClickListener {
                onProductClick(item)
            }
        }
    }
    fun updateData(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }


    override fun getItemCount() = items.size
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

            val cartQty = CartManager.getItemQuantity(item.id)
            binding.textQty.text = cartQty.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        if (item.stock != 0) {
            holder.binding.btnPlus.setOnClickListener {
                val currentCartQty = CartManager.getItemQuantity(item.id)
                if (currentCartQty < item.stock) {
                    CartManager.addToCart(item, 1)
                    holder.bind(item)
                    Toast.makeText(context, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Stock limit reached", Toast.LENGTH_SHORT).show()
                }
            }

            holder.binding.btnMinus.setOnClickListener {
                val currentCartQty = CartManager.getItemQuantity(item.id)
                if (currentCartQty > 0) {
                    CartManager.updateQuantity(item.id, currentCartQty - 1)
                    holder.bind(item)
                }
            }
        }
    }

    fun updateData(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}
