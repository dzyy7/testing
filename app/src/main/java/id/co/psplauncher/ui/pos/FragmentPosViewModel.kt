package id.co.psplauncher.ui.pos

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.psplauncher.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.Utils.formatCurrency
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.balance.BalanceRepository
import id.co.psplauncher.data.network.chip.ChipRepository
import id.co.psplauncher.data.network.dashboard.DashboardRepository
import id.co.psplauncher.data.network.response.Content
import id.co.psplauncher.data.network.response.Product
import id.co.psplauncher.databinding.FragmentPosBinding
import id.co.psplauncher.ui.pos.adapter.BalanceAdapter
import id.co.psplauncher.ui.pos.adapter.BalanceCard
import id.co.psplauncher.ui.pos.adapter.BalanceType
import id.co.psplauncher.ui.pos.adapter.CategoryAdapter
import id.co.psplauncher.ui.pos.adapter.GridProductAdapter
import id.co.psplauncher.ui.pos.adapter.ListProductAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FragmentPosViewModel @Inject constructor(
    private val chipRepository: ChipRepository,
    private val dashboardRepository: DashboardRepository,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    // State Data
    private val _categories = MutableLiveData<List<Content>>()
    val categories: LiveData<List<Content>> = _categories

    private var _allProductsList: List<Product> = emptyList()

    private val _displayedProducts = MutableLiveData<List<Product>>()
    val displayedProducts: LiveData<List<Product>> = _displayedProducts

    private val _balanceCards = MutableLiveData<List<BalanceCard>>()

    // Grid / List toggle state
    private val _isGridMode = MutableLiveData(true)
    val isGridMode: LiveData<Boolean> = _isGridMode

    // Pull-to-refresh state
    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    // Adapter references untuk refresh cart state
    private var gridAdapter: GridProductAdapter? = null
    private var listAdapter: ListProductAdapter? = null

    // --- Filter state ---
    private var currentCategoryId: String? = null
    private var currentSearchQuery: String = ""
    private var currentSortType: SortType = SortType.DEFAULT

    enum class SortType {
        DEFAULT,
        NAME_DESC,  // Z-A
        PRICE_ASC,  // Harga terendah
        PRICE_DESC  // Harga tertinggi
    }

    // Init
    init {
        fetchBalanceData()
        fetchCategories()
        fetchProducts()
    }

    // Pull-to-refresh: refresh semua data sekaligus
    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchBalanceData()
                fetchCategories()
                fetchProducts()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // Dipanggil dari Fragment saat CartManager.cartItems flow emit
    fun refreshAdapterCartState() {
        if (_isGridMode.value == true) {
            gridAdapter?.refreshCartBadges()
        } else {
            listAdapter?.refreshCartQtys()
        }
    }

    fun fetchBalanceData() = viewModelScope.launch {
        try {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val response = balanceRepository.fetchBalance(currentDate)
            if (response is Resource.Success) {
                val data = response.value
                val balanceCards = listOf(
                    BalanceCard(
                        label = "Saldo Tunai",
                        amount = "Rp. ${formatCurrency(data.cash)}",
                        lastTransaction = "-",
                        type = BalanceType.CASH
                    ),
                    BalanceCard(
                        label = "Saldo PSP",
                        amount = "Rp. ${formatCurrency(data.psp)}",
                        lastTransaction = "-",
                        type = BalanceType.PSP
                    )
                )
                _balanceCards.value = balanceCards
            } else {
                Log.e("PosViewModel", "Balance API Error")
            }
        } catch (e: Exception) {
            Log.e("PosViewModel", "Balance Exception: ${e.message}")
        }
    }

    fun fetchCategories() = viewModelScope.launch {
        try {
            val response = chipRepository.fetchData()
            if (response is Resource.Success) {
                val list = response.value.content ?: emptyList()
                _categories.value = list
            } else {
                Log.e("PosViewModel", "Category API Error")
            }
        } catch (e: Exception) {
            Log.e("PosViewModel", "Category Exception: ${e.message}")
        }
    }

    fun fetchProducts() = viewModelScope.launch {
        try {
            val response = dashboardRepository.fetchData()
            if (response is Resource.Success) {
                val products = response.value.content ?: emptyList()
                _allProductsList = products
                applyFilterAndSearch()
            } else {
                Log.e("PosViewModel", "Product API Error")
            }
        } catch (e: Exception) {
            Log.e("PosViewModel", "Product Exception: ${e.message}")
        }
    }

    // Filter & Search

    fun filterByCategory(categoryId: String) {
        currentCategoryId = categoryId
        applyFilterAndSearch()
    }

    fun searchProduct(query: String) {
        currentSearchQuery = query
        applyFilterAndSearch()
    }

    fun sortBy(sortType: SortType) {
        currentSortType = sortType
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        var result = _allProductsList

        if (!currentCategoryId.isNullOrEmpty()) {
            result = result.filter { it.category.id == currentCategoryId }
        }

        if (currentSearchQuery.isNotEmpty()) {
            result = result.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        result = when (currentSortType) {
            SortType.NAME_DESC  -> result.sortedByDescending { it.name.lowercase() }
            SortType.PRICE_ASC  -> result.sortedBy { it.sellingPrice }
            SortType.PRICE_DESC -> result.sortedByDescending { it.sellingPrice }
            SortType.DEFAULT    -> result
        }

        _displayedProducts.value = result
    }

    // UI Setup – Balance

    fun setupBalanceCarousel(lifecycleOwner: LifecycleOwner, context: Context, binding: FragmentPosBinding) {
        _balanceCards.observe(lifecycleOwner) { cards ->
            val balanceAdapter = BalanceAdapter(cards)
            binding.vpBalance.adapter = balanceAdapter

            val itemCount = cards.size
            setupCustomIndicators(context, binding, itemCount)

            binding.vpBalance.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(binding, position, itemCount)
                }
            })
        }
    }

    private fun setupCustomIndicators(context: Context, binding: FragmentPosBinding, count: Int) {
        binding.indicatorContainer.removeAllViews()
        val marginPx = (4 * context.resources.displayMetrics.density).toInt()

        for (i in 0 until count) {
            val imageView = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginPx, 0, marginPx, 0)
                }
                setImageResource(if (i == 0) R.drawable.indicator_active else R.drawable.indicator_inactive)
            }
            binding.indicatorContainer.addView(imageView)
        }
    }

    private fun updateIndicators(binding: FragmentPosBinding, position: Int, count: Int) {
        for (i in 0 until count) {
            val imageView = binding.indicatorContainer.getChildAt(i) as? ImageView
            if (i == position) {
                imageView?.setImageResource(R.drawable.indicator_active)
            } else {
                imageView?.setImageResource(R.drawable.indicator_inactive)
            }
        }
    }

    // UI Setup – Category
    fun setupCategoryRecycler(lifecycleOwner: LifecycleOwner, context: Context, binding: FragmentPosBinding) {
        val categoryAdapter = CategoryAdapter(emptyList()) { category ->
            filterByCategory(category.id)
            Log.d("PosViewModel", "Category Selected: ${category.name}")
        }

        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        categories.observe(lifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                categoryAdapter.updateData(list)
            } else {
                Log.e("PosViewModel", "List Category Kosong/Null")
            }
        }
    }

    // UI Setup – Product RecyclerView
    fun setupProductRecycler(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        binding: FragmentPosBinding,
        onGridProductClick: (Product) -> Unit
    ) {
        gridAdapter = GridProductAdapter(
            items = emptyList(),
            context = context,
            onProductClick = onGridProductClick
        )

        listAdapter = ListProductAdapter(
            items = emptyList(),
            context = context,
            onProductClick = { product ->
                Log.d("PosViewModel", "Product clicked (list): ${product.name}")
            }
        )

        binding.recyclerViewListing.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = gridAdapter
        }

        isGridMode.observe(lifecycleOwner) { isGrid ->
            if (isGrid) {
                binding.recyclerViewListing.layoutManager = GridLayoutManager(context, 2)
                binding.recyclerViewListing.adapter = gridAdapter
                gridAdapter?.updateData(_displayedProducts.value ?: emptyList())
            } else {
                binding.recyclerViewListing.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.recyclerViewListing.adapter = listAdapter
                listAdapter?.updateData(_displayedProducts.value ?: emptyList())
            }
        }

        _displayedProducts.observe(lifecycleOwner) { list ->
            if (_isGridMode.value == true) {
                gridAdapter?.updateData(list)
            } else {
                listAdapter?.updateData(list)
            }
        }
    }

    // UI Setup – Grid / List Toggle
    fun setupViewToggle(lifecycleOwner: LifecycleOwner, context: Context, binding: FragmentPosBinding) {
        fun applyToggleVisual(isGrid: Boolean) {
            if (isGrid) {
                binding.frameGridView.background =
                    context.getDrawable(R.drawable.bg_btn_toggle_selected)
                binding.btnGridView.setColorFilter(0xFF7B00AB.toInt())
                binding.frameListView.background =
                    context.getDrawable(R.drawable.bg_btn_toggle_default)
                binding.btnListView.setColorFilter(0xFF9E9E9E.toInt())
            } else {
                binding.frameListView.background =
                    context.getDrawable(R.drawable.bg_btn_toggle_selected)
                binding.btnListView.setColorFilter(0xFF7B00AB.toInt())
                binding.frameGridView.background =
                    context.getDrawable(R.drawable.bg_btn_toggle_default)
                binding.btnGridView.setColorFilter(0xFF9E9E9E.toInt())
            }
        }

        isGridMode.observe(lifecycleOwner) { isGrid ->
            applyToggleVisual(isGrid)
        }

        binding.frameGridView.setOnClickListener { _isGridMode.value = true }
        binding.frameListView.setOnClickListener { _isGridMode.value = false }
    }

    // UI Setup – Search
    fun setupSearchListener(binding: FragmentPosBinding) {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchProduct(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}