package id.co.psplauncher.ui.pos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.co.psplauncher.R

enum class BalanceType {
    CASH,
    PSP
}

data class BalanceCard(
    val label: String,
    val amount: String,
    val lastTransaction: String,
    val type: BalanceType = BalanceType.CASH,
    var isVisible: Boolean = false
)

class BalanceAdapter(
    private val balanceCards: List<BalanceCard>
) : RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        val layoutRes = if (viewType == VIEW_TYPE_PSP) {
            R.layout.item_balance_card_gold
        } else {
            R.layout.item_balance_card
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, false)
        return BalanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        holder.bind(balanceCards[position])
    }

    override fun getItemCount() = balanceCards.size

    override fun getItemViewType(position: Int): Int {
        return if (balanceCards[position].type == BalanceType.PSP) {
            VIEW_TYPE_PSP
        } else {
            VIEW_TYPE_CASH
        }
    }

    inner class BalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBalanceLabel: TextView = itemView.findViewById(R.id.tvBalanceLabel)
        private val tvBalanceAmount: TextView = itemView.findViewById(R.id.tvBalanceAmount)
        private val tvLastTransactionAmount: TextView = itemView.findViewById(R.id.tvLastTransactionAmount)
        private val imgEye: ImageView = itemView.findViewById(R.id.imgEye)

        private var isBalanceVisible = false

        fun bind(balanceCard: BalanceCard) {
            tvBalanceLabel.text = balanceCard.label
            tvLastTransactionAmount.text = balanceCard.lastTransaction

            updateBalanceUI(balanceCard)

            imgEye.setOnClickListener {
                balanceCard.isVisible = !balanceCard.isVisible
                updateBalanceUI(balanceCard)
            }
        }

        private fun updateBalanceUI(balanceCard: BalanceCard) {
            if (balanceCard.isVisible) {
                tvBalanceAmount.text = balanceCard.amount
                imgEye.setImageResource(R.drawable.ic_eye_open)
            } else {
                tvBalanceAmount.text = "******"
                imgEye.setImageResource(R.drawable.ic_eye_closed)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_CASH = 0
        private const val VIEW_TYPE_PSP = 1
    }
}
