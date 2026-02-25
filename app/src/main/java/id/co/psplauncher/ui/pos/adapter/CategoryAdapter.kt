package id.co.psplauncher.ui.pos.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.co.psplauncher.R
import id.co.psplauncher.data.network.response.Content
import id.co.psplauncher.databinding.ItemCategoryChipBinding

class CategoryAdapter(
    private var items: List<Content>,
    private val onCategoryClick: (Content) -> Unit,
    ): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class CategoryViewHolder(val binding: ItemCategoryChipBinding):
        RecyclerView.ViewHolder(binding.root){
            fun bind(item: Content, isSelected: Boolean){
                val chip = binding.textCategory
                val context = binding.root.context

                chip.text = item.name

                val mainPurple = ContextCompat.getColor(context, R.color.mainPurple)
                val white = ContextCompat.getColor(context, R.color.white)
                val textDark = ContextCompat.getColor(context, R.color.textDark) // Warna teks biasa
                val bgUnselected = ContextCompat.getColor(context, R.color.input_stroke)

                if(isSelected){
                    chip.chipBackgroundColor = ColorStateList.valueOf(white)
                    chip.chipStrokeColor = ColorStateList.valueOf(mainPurple)
                    chip.chipStrokeWidth = 4f
                    chip.setTextColor(mainPurple)
                }else{
                    chip.chipBackgroundColor = ColorStateList.valueOf(bgUnselected)
                    chip.chipStrokeColor = ColorStateList.valueOf(white)
                    chip.chipStrokeWidth = 4f
                    chip.setTextColor(textDark)
                }
                chip.setOnClickListener {
                    val previousPosition = selectedPosition

                    selectedPosition =
                        if (bindingAdapterPosition == selectedPosition) {
                            RecyclerView.NO_POSITION // toggle off
                        } else {
                            bindingAdapterPosition
                        }

                    if (previousPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousPosition)
                    }
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(selectedPosition)
                        onCategoryClick(item)
                    } else {
                        onCategoryClick(Content(id = "", name = "ALL")) // atau trigger reset filter
                    }
                }

            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryViewHolder(binding)
        }
        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.bind(items[position], position == selectedPosition)
        }
        override fun getItemCount() = items.size
        fun updateData(newList: List<Content>) {
            items = newList
            notifyDataSetChanged()
        }
    }