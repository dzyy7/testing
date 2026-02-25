package id.co.psplauncher.ui.fragments.item_detail

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.FragmentItemDetailBinding

@AndroidEntryPoint
class FragmentItemDetail : DialogFragment() {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FragmentItemDetailViewModel by viewModels()
    private var productId: String? = null

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: String): FragmentItemDetail {
            return FragmentItemDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCT_ID, productId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getString(ARG_PRODUCT_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // ✅ FIX: Set dialog width to MATCH_PARENT dengan margin horizontal
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Existing code...
            val width = (resources.displayMetrics.widthPixels * 0.92).toInt()
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("dismiss_item_detail", Bundle())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        productId?.let { viewModel.fetchProductDetail(it) }
    }

    private fun setupObservers() {
        viewModel.productDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnAddToCart.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnAddToCart.isEnabled = true
                    bindProductData(resource.value)
                }
                is Resource.Failure -> {
                    binding.btnAddToCart.isEnabled = false
                    Toast.makeText(requireContext(), "Failed to load product", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.quantity.observe(viewLifecycleOwner) { qty ->
            binding.tvCount.text = qty.toString()
            binding.tvSelectedItem.text = "$qty Item Selected"
        }

        viewModel.addToCartSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun bindProductData(product: id.co.psplauncher.data.network.response.Product) {
        binding.tvTitle.text = product.name
        binding.tvDescription.text = product.description ?: "No description"
        binding.tvPrice.text = "Rp. ${formatCurrency(product.sellingPrice)}"
        binding.tvStock.text = "Stock: ${product.stock}"

        val imageUrl = product.images.firstOrNull()?.thumbnail
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgProduct)
        } else {
            binding.imgProduct.setImageResource(R.drawable.ic_launcher_background)
        }

        if (product.stock <= 0) {
            binding.btnAddToCart.isEnabled = false
            binding.btnAddToCart.text = "Out of Stock"
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnMinus.setOnClickListener {
            viewModel.decreaseQuantity()
        }

        binding.btnPlus.setOnClickListener {
            viewModel.increaseQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
            if (!viewModel.addToCart()) {
                Toast.makeText(requireContext(), "Cannot add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}