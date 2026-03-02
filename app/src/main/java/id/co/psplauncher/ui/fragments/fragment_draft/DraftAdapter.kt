package id.co.psplauncher.ui.fragments.fragment_draft

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.response.DraftItem

class DraftAdapter(
    private var items: List<DraftItem>,
    private val onDraftClick: (DraftItem) -> Unit,
    private val onDeleteClick: (DraftItem) -> Unit
) : RecyclerView.Adapter<DraftAdapter.DraftViewHolder>() {

    private var expandedPosition = RecyclerView.NO_POSITION

    inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDraftName: TextView = itemView.findViewById(R.id.tvDraftName)
        val tvDraftDate: TextView = itemView.findViewById(R.id.tvDraftDate)
        val tvDraftTotal: TextView = itemView.findViewById(R.id.tvDraftTotal)
        val tvItemCount: TextView = itemView.findViewById(R.id.tvItemCount)
        val tvChevron: TextView = itemView.findViewById(R.id.tvChevron)
        val layoutDetail: LinearLayout = itemView.findViewById(R.id.layoutDetail)
        val layoutDetailItems: LinearLayout = itemView.findViewById(R.id.layoutDetailItems)
        val btnLoadDraft: TextView = itemView.findViewById(R.id.btnLoadDraft)
        val btnDeleteDraft: ImageView = itemView.findViewById(R.id.btnDeleteDraft)

        fun bind(item: DraftItem, isExpanded: Boolean) {
            tvDraftName.text = item.draftName
            tvDraftDate.text = item.createDate
            tvDraftTotal.text = "Rp. ${formatCurrency(item.totalPrice)}"
            tvItemCount.text = "${item.itemCount} item"
            tvChevron.text = if (isExpanded) "▲" else "▼"
            layoutDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE

            if (isExpanded) {
                layoutDetailItems.removeAllViews()
                item.items.forEach { product ->
                    val tv = TextView(itemView.context).apply {
                        text = "• ${product.productName}  ×${product.quantity}  Rp. ${formatCurrency(product.buyingPrice * product.quantity)}"
                        textSize = 12f
                        setTextColor(0xFF666666.toInt())
                        setPadding(0, 4, 0, 4)
                    }
                    layoutDetailItems.addView(tv)
                }
            }

            itemView.setOnClickListener {
                val previousExpanded = expandedPosition
                expandedPosition = if (isExpanded) RecyclerView.NO_POSITION else bindingAdapterPosition
                if (previousExpanded != RecyclerView.NO_POSITION) notifyItemChanged(previousExpanded)
                notifyItemChanged(bindingAdapterPosition)
            }

            btnLoadDraft.setOnClickListener { onDraftClick(item) }
            btnDeleteDraft.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_draft, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        holder.bind(items[position], position == expandedPosition)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<DraftItem>) {
        items = newItems
        expandedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}